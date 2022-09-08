package com.minzheng.blog.controller;


import com.minzheng.blog.entity.TbReceviver;
import com.minzheng.blog.service.feign.TbReceiverService;

import com.minzheng.blog.util.UserUtils;
import com.minzheng.blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.List;


/**
 * (TbReceviver)表控制层
 *
 * @author lihuijun
 * @since 2022-09-04 09:09:12
 */

@RestController
@Api("用户收件人")
public class TbReceviverController {
/**
     * 服务对象*/


    @Resource
    private TbReceiverService tbReceviverService;

/**
      分页查询当前登录用户的接收者*/


    @ApiOperation(value = "分页查询当前登录用户的接收者")
    @GetMapping("/admin/receiver")
    public Result<List<TbReceviver>> listUserReceiver(Integer current,Integer size) {
        return tbReceviverService.listUserReceiver(current,size, UserUtils.getLoginUser().getUserInfoId());
    }

    @ApiOperation("新增好友信息")
    @PostMapping("/admin/receiver")
    public Result<String> save(@RequestBody TbReceviver tbReceviver){
        return tbReceviverService.saveReceiver(tbReceviver,UserUtils.getLoginUser().getUserInfoId());
    }
    @ApiOperation("修改好友信息")
    @PutMapping("/admin/receiver")
    public Result<String> update(@RequestBody TbReceviver tbReceviver){
        return tbReceviverService.updateReceiver(tbReceviver);
    }
    @ApiOperation("删除好友信息")
    @DeleteMapping("/admin/receiver/{id}")
    public Result<String> delete(@PathVariable("id") Integer id){
        return tbReceviverService.deleteReceiver(id);
    }
}
