package com.szm.demo.controller;

import com.szm.demo.common.ApiConstant;
import com.szm.demo.common.Result;
import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserLoginResp;
import com.szm.demo.dto.UserRegisterReq;
import com.szm.demo.service.UserService;
import com.szm.demo.util.JWTUtil;
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
    @Autowired
    private JWTUtil jWTUtil;

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

    @PostMapping("/player/create")
    public Result<String> createPlayer(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        userService.createDefaultPlayer(userId);
        return Result.success("创建角色成功");
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        userService.logout(request.getHeader("Authorization"));
        return Result.success("已登出");
    }
}
