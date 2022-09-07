package com.minzheng.blog.dto;

import com.minzheng.blog.entity.TbWeatherpush;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 醉后不知天在水，满船清梦压星河。
 *
 * @author lhj
 * @date 2022/09/05 00:38
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushDTO extends TbWeatherpush {
    //这里需要有收件人的信息，这里的接收者是指微信模板中的人的信息
    private String receiverName;
}


