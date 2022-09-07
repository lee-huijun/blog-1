package com.li.weatherpush.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 醉后不知天在水，满船清梦压星河。
 *
 * @author lhj
 * @date 2022/08/28 15:09
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_weatherpush")
public class WeatherPush {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id; //表主键
    private Integer userId; //是本系统的用户id
    private String wxAppid; //微信appid
    private String wxSecret; //微信secret
    private String pushTime; //推送时间
    private String modelId; //模板id
    private Integer receiverId; //接收者id
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
