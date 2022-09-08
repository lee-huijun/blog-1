package com.li.weatherpush.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.li.weatherpush.entity.Result;
import com.li.weatherpush.entity.TbReceviver;
import com.li.weatherpush.service.TbReceviverService;
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
     * 服务对象
     */
    @Resource
    private TbReceviverService tbReceviverService;

    /**
     * 分页查询当前登录用户的接收者，因为是微服务所以需要通过参数来获取登录blog微服务的用户，不能使用feign来获取，因为去访问的blog的时候
     * 是以新用户去访问的，所以需要通过参数来获取
     */
    @ApiOperation(value = "分页查询当前登录用户的接收者")
    @GetMapping("/admin/receiver")
    public Result<List<TbReceviver>> listUserReceiver(Integer current, Integer size,Integer userId) {
        Page<TbReceviver> page = new Page<>(current, size);
        LambdaQueryWrapper<TbReceviver> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TbReceviver::getUserId, userId);
        Page<TbReceviver> ReceiverPage = tbReceviverService.page(page, queryWrapper);
        return Result.ok(ReceiverPage.getRecords());
    }

    @ApiOperation("新增好友信息")
    @PostMapping("/admin/receiver")
    public Result<String> save(@RequestBody TbReceviver tbReceviver,@RequestParam("userId") Integer userId){
        tbReceviver.setUserId(userId);
        tbReceviverService.saveOrUpdate(tbReceviver);
        return Result.ok();
    }
    @ApiOperation("修改好友信息")
    @PutMapping("/admin/receiver")
    public Result<String> update(@RequestBody TbReceviver tbReceviver){
        tbReceviverService.updateById(tbReceviver);
        return Result.ok();
    }
    @ApiOperation("删除好友信息")
    @DeleteMapping("/admin/receiver/{id}")
    public Result<String> delete(@PathVariable("id") Integer id){
        tbReceviverService.removeById(id);
        return Result.ok();
    }
}