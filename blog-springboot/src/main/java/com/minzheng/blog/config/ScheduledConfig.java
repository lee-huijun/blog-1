package com.minzheng.blog.config;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.minzheng.blog.entity.TbReceviver;
import com.minzheng.blog.entity.TbWeatherpush;
import com.minzheng.blog.entity.WeatherInfo;
import com.minzheng.blog.service.TbWeatherpushService;
import com.minzheng.blog.util.GaodeUtil;
import com.minzheng.blog.util.RandomAncientPoetry;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.minzheng.blog.config.TemplateDataBuilder.buildData;
import static com.minzheng.blog.config.TemplateDataBuilder.getProperty;
import static com.minzheng.blog.util.GaodeUtil.getAdcCode;

/**
 * 醉后不知天在水，满船清梦压星河。
 *
 * @author lhj
 * @date 2022/09/06 23:33
 **/
@Configuration
public class ScheduledConfig implements SchedulingConfigurer {
    @Autowired
    private TbWeatherpushService weatherpushService;

    private ScheduledTaskRegistrar taskRegistrar;

    @Autowired
    private IService<TbReceviver> receviverService;


    private Set<ScheduledFuture<?>> scheduledFutures = null;
    private Map<String, ScheduledFuture<?>> taskFutures = new ConcurrentHashMap<>();

    /**
     * 这个方法在Spring初始化的时候会帮我们执行，这里也会拉取数据库内所有需要执行的任务，进行添加到定时器里。
     * 这个相当于初始化数据库中已经存在的任务
     * @param scheduledTaskRegistrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        List<TriggerTask> list= new ArrayList<>();
        //查询出来当前数据库中存储的所有有效的任务
        List<TbWeatherpush> maps = weatherpushService.list();
        //循环添加任务
        maps.forEach(weatherpush->{
            TriggerTask triggerTask = new TriggerTask(()->{
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
                        .toUser(tbReceviver.getWxId())
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
            });
            list.add(triggerTask);
        });
        //将任务列表注册到定时器
        scheduledTaskRegistrar.setTriggerTasksList(list);
        //将注册器赋值给全局变量，方便后面使用
        this.taskRegistrar = scheduledTaskRegistrar;

    }


    /**
     * 添加任务
     * @param taskId 这里使用tb_weatherpush表的id作为任务id
     * @param triggerTask 这里需要传入一个TriggerTask对象，就是直接new一个TriggerTask对象，这个trigger对象中包含了上面的任务和执行时间。
     */
    public void addTask(String taskId, TriggerTask triggerTask) {
        //如果定时任务id已存在，则取消原定时器，从新创建新定时器，这里也是个更新定时任务的过程。
        if (taskFutures.containsKey(taskId)) {
            System.out.println("the taskId[" + taskId + "]  取消，重新添加");
            cancelTriggerTask(taskId);
        }
        TaskScheduler scheduler = taskRegistrar.getScheduler();
        ScheduledFuture<?> future = scheduler.schedule(triggerTask.getRunnable(), triggerTask.getTrigger());
        getScheduledFutures().add(future);
        taskFutures.put(taskId, future);
    }

    /**
     * 获取任务列表
     */
    private Set<ScheduledFuture<?>> getScheduledFutures() {
        if (scheduledFutures == null) {
            try {
                scheduledFutures = (Set<ScheduledFuture<?>>) getProperty(taskRegistrar, "scheduledTasks");
            } catch (NoSuchFieldException e) {
                throw new SchedulingException("not found scheduledFutures field.");
            }
        }
        return scheduledFutures;
    }



    /**
     * 取消任务
     */
    public void cancelTriggerTask(String taskId) {
        ScheduledFuture<?> future = taskFutures.get(taskId);
        if (future != null) {
            future.cancel(true);
        }
        taskFutures.remove(taskId);
        getScheduledFutures().remove(future);
    }





}

