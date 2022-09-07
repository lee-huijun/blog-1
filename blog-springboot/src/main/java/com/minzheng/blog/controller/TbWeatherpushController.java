package com.minzheng.blog.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.minzheng.blog.config.ScheduledConfig;
import com.minzheng.blog.config.TemplateDataBuilder;
import com.minzheng.blog.dto.PushDTO;
import com.minzheng.blog.entity.TbReceviver;
import com.minzheng.blog.entity.TbWeatherpush;
import com.minzheng.blog.entity.WeatherInfo;
import com.minzheng.blog.service.TbReceviverService;
import com.minzheng.blog.service.TbWeatherpushService;
import com.minzheng.blog.util.GaodeUtil;
import com.minzheng.blog.util.RandomAncientPoetry;
import com.minzheng.blog.util.UserUtils;
import com.minzheng.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.minzheng.blog.config.TemplateDataBuilder.buildData;
import static com.minzheng.blog.util.GaodeUtil.getAdcCode;

/**
 * (TbWeatherpush)表控制层
 *
 * @author lihuijun
 * @since 2022-09-03 00:51:45
 */
@RestController
@Api(tags = "推送设置")
@EnableScheduling
public class TbWeatherpushController {
    /**
     * 服务对象
     */
    @Resource
    private TbWeatherpushService tbWeatherpushService;
    @Autowired
    private TbReceviverService tbReceviverService;
    @Autowired
    private IService<TbReceviver> receviverService;
    @Autowired
    ScheduledConfig scheduledConfig;


    //这里直接创建一个定时任务，这个任务中要包含微信公众号信息，这里就不将微信公众号信息单独拿出来了
    @ApiOperation(value = "获取当前用户的推送设置")
    @GetMapping("/admin/weatherpush")
    public Result<List<PushDTO>> page(Integer current, Integer size) {
        //这里需要组装一下
        Page<TbWeatherpush> weatherPage = new Page<>(current, size);
        LambdaQueryWrapper<TbWeatherpush> queryWrapper = new LambdaQueryWrapper<TbWeatherpush>();
        queryWrapper.eq(TbWeatherpush::getUserId, UserUtils.getLoginUser().getUserInfoId());
        Page<TbWeatherpush> page = tbWeatherpushService.page(weatherPage, queryWrapper);
        List<PushDTO> result = page.getRecords().stream()
                .map(weather -> {
                    PushDTO target = new PushDTO();
                    BeanUtils.copyProperties(weather, target);
                    //这里需要查询一下接收人
                    TbReceviver receviver = tbReceviverService.getById(weather.getReceiverId());
                    target.setReceiverName(receviver.getName());
                    return target;
                })
                .collect(Collectors.toList());

        //这里应该是在新建的时候发送一条请求去获取当前用户的微信推送接收人信息，但是显示的时候应该显示朋友的姓名
        //所以还是要组装一下

        //根据接收人id获取接收人信息
        return Result.ok(result);
    }

    @ApiOperation(value = "添加推送设置")
    @PostMapping("/admin/weatherpush")
    public Result save(@RequestBody PushDTO pushDTO) {
        TbWeatherpush weatherpush = new TbWeatherpush();
        BeanUtils.copyProperties(pushDTO, weatherpush);
        weatherpush.setUserId(UserUtils.getLoginUser().getUserInfoId());
        tbWeatherpushService.saveOrUpdate(weatherpush);

        //添加定时任务
        try {
            Map<String, ScheduledFuture<?>> taskRegistrar = (Map<String, ScheduledFuture<?>>) TemplateDataBuilder.getProperty(scheduledConfig, "taskFutures");
            scheduledConfig.addTask(String.valueOf(weatherpush.getReceiverId()),
                    new TriggerTask(()->{
                        //这是任务执行的方法，这里可以写你的业务逻辑
                        System.out.println("执行定时任务："+ LocalDateTime.now().toLocalTime());
                        //配置微信公众号
                        System.out.println("执行动态定时任务: " + System.currentTimeMillis());
                        AtomicReference<WxMpService> serviceAtomicReference = new AtomicReference<>();
                        WxMpDefaultConfigImpl mpConfig = new WxMpDefaultConfigImpl();
                        mpConfig.setAppId(weatherpush.getWxAppid());
                        mpConfig.setSecret(weatherpush.getWxSecret());
                        serviceAtomicReference.set(new WxMpServiceImpl());
                        serviceAtomicReference.get().setWxMpConfigStorage(mpConfig);

                        //查询收件人信息
                        LambdaQueryWrapper<TbReceviver> tbReceviverLambdaQueryWrapper = new LambdaQueryWrapper<>();
                        tbReceviverLambdaQueryWrapper.eq(TbReceviver::getId, weatherpush.getReceiverId());
                        TbReceviver tbReceviver = receviverService.getOne(tbReceviverLambdaQueryWrapper);

                        //新建消息模板
                        WxMpTemplateMessage message = WxMpTemplateMessage.builder()
                                .url("http://lihuijun.fun")
                                .toUser(weatherpush.getReceiverId() + "")
                                .templateId(weatherpush.getModelId())
                                .data(buildData(tbReceviver))
                                .build();

                        //发送消息
                        Optional.ofNullable(serviceAtomicReference.get())
                                .map(WxMpService::getTemplateMsgService)
                                .ifPresent(
                                        service -> {
                                            try {
                                                service.sendTemplateMsg(message);
                                            } catch (Exception e) {
                                                System.err.println("发送消息失败: " + e.getMessage());
                                            }
                                        });
                    },triggerContext -> {
                        //执行任务的时间,从数据库中获取推送时间
                        return new CronTrigger(weatherpush.getPushTime()).nextExecutionTime(triggerContext);
                    }));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


        return Result.ok();
    }


    @ApiOperation(value = "删除推送设置")
    @DeleteMapping("/admin/weatherpush/{id}")
    public Result delete(@PathVariable Integer id) {
        tbWeatherpushService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value="获取当前用户的好友")
    @GetMapping("/admin/weatherpush/friends")
    public Result<List<TbReceviver>> getFriends(){
        LambdaQueryWrapper<TbReceviver> queryWrapper = new LambdaQueryWrapper<TbReceviver>();
        queryWrapper.eq(TbReceviver::getUserId,UserUtils.getLoginUser().getUserInfoId());
        List<TbReceviver> list = tbReceviverService.list(queryWrapper);
        return Result.ok(list);
    }





}
