package com.szm.demo.controller;

import com.szm.demo.common.ApiConstant;
import com.szm.demo.common.Result;
import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserLoginResp;
import com.szm.demo.dto.UserRegisterReq;
import com.szm.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ApiConstant.API_USERS)
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterReq req) {
        userService.register(req);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<UserLoginResp> login(@RequestBody UserLoginReq req) {
        UserLoginResp resp = new UserLoginResp(userService.login(req));
        return Result.success("登录成功", resp);
    }


    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        userService.logout(request.getHeader("Authorization"));
        return Result.success("已登出");
    }
}
