package com.minzheng.blog.service.feign;

import com.minzheng.blog.dto.PushDTO;
import com.minzheng.blog.entity.TbReceviver;
import com.minzheng.blog.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "localhost:9001")
public interface TbReceiverService {
    @GetMapping("/admin/receiver")
    Result<List<TbReceviver>> listUserReceiver(@RequestParam("current") Integer current,@RequestParam("size") Integer size
    ,@RequestParam("userId") Integer userId);
    @PostMapping("/admin/receiver")
    Result<String> saveReceiver(@RequestBody TbReceviver tbReceviver,@RequestParam("userId") Integer userId);
    @PutMapping("/admin/receiver")
    Result<String> updateReceiver(@RequestBody TbReceviver tbReceviver);
    @DeleteMapping("/admin/receiver/{id}")
    Result<String> deleteReceiver(@PathVariable("id") Integer id);

    @GetMapping("/admin/weatherpush")
    public Result<List<PushDTO>> pageWeatherpush(@RequestParam("current") Integer current,@RequestParam("size") Integer size
            ,@RequestParam("userId") Integer userId);
    @PostMapping("/admin/weatherpush")
    public Result saveWeatherpush(@RequestBody PushDTO pushDTO,@RequestParam("userId") Integer userId);
    @DeleteMapping("/admin/weatherpush/{id}")
    public Result deleteWeatherpush(@PathVariable Integer id);
    @GetMapping("/admin/weatherpush/friends")
    public Result<List<TbReceviver>> getFriends(@RequestParam("userId") Integer userId);
}
