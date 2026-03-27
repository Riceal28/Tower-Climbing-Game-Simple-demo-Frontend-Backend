package com.szm.demo.controller;

import com.szm.demo.common.ApiConstant;
import com.szm.demo.common.Result;
import com.szm.demo.service.PlayerService;
import com.szm.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = ApiConstant.API_PLAYER)
public class PlayerController {
    @Autowired
    PlayerService playerService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public Result<String> createPlayer(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        playerService.createDefaultPlayer(userId);
        return Result.success("创建角色成功");
    }
    @PostMapping("/levelup")
    public Result<String> playerLevelUP(HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        userService.setExp(userId,210L);
        playerService.tryLevelUp(userId);
        return Result.success("升级成功");
    }
    @PostMapping("/reset")
    public Result<String> playerReset(HttpServletRequest request){
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        playerService.resetPlayer(userId);
        return Result.success("角色重置成功");
    }
}
