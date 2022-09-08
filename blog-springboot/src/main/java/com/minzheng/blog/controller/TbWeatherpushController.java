
package com.minzheng.blog.controller;


import com.minzheng.blog.dto.PushDTO;
import com.minzheng.blog.entity.TbReceviver;

import com.minzheng.blog.service.feign.TbReceiverService;

import com.minzheng.blog.util.UserUtils;
import com.minzheng.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;


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
    private TbReceiverService tbWeatherpushService;


    //这里直接创建一个定时任务，这个任务中要包含微信公众号信息，这里就不将微信公众号信息单独拿出来了
    @ApiOperation(value = "获取当前用户的推送设置")
    @GetMapping("/admin/weatherpush")
    public Result<List<PushDTO>> page(Integer current, Integer size) {
        return tbWeatherpushService.pageWeatherpush(current, size, UserUtils.getLoginUser().getUserInfoId());
    }

    @ApiOperation(value = "添加推送设置")
    @PostMapping("/admin/weatherpush")
    public Result save(@RequestBody PushDTO pushDTO) {
        return tbWeatherpushService.saveWeatherpush(pushDTO, UserUtils.getLoginUser().getUserInfoId());
    }


    @ApiOperation(value = "删除推送设置")
    @DeleteMapping("/admin/weatherpush/{id}")
    public Result delete(@PathVariable Integer id) {
        return tbWeatherpushService.deleteWeatherpush(id);
    }

    @ApiOperation(value="获取当前用户的好友")
    @GetMapping("/admin/weatherpush/friends")
    public Result<List<TbReceviver>> getFriends(){
        return tbWeatherpushService.getFriends(UserUtils.getLoginUser().getUserInfoId());
    }





}

