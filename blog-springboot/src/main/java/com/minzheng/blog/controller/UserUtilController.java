package com.minzheng.blog.controller;

import com.minzheng.blog.dto.UserDetailDTO;
import com.minzheng.blog.util.UserUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 醉后不知天在水，满船清梦压星河。
 *
 * @author lhj
 * @date 2022/09/07 19:46
 **/
@RestController
public class UserUtilController {
    @RequestMapping("/util/user")
    public String getUser() {
        UserDetailDTO loginUser = UserUtils.getLoginUser();
        Integer userInfoId = loginUser.getUserInfoId();
        return userInfoId.toString();
    }
}
