package com.szm.demo.controller;

import com.szm.demo.common.ApiConstant;
import com.szm.demo.common.PlayerClass;
import com.szm.demo.common.Result;
import com.szm.demo.dto.PlayerCreateResp;
import com.szm.demo.dto.PlayerShowResp;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.service.PlayerProviderService;
import com.szm.demo.service.PlayerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = ApiConstant.API_PLAYER)
public class PlayerController {

    @Autowired
    PlayerService playerService;
    @Autowired
    private PlayerProviderService playerProviderService;

    @GetMapping("/showall")
    public Result<List<PlayerShowResp>> showAllPlayer(){
        return Result.success(playerService.showAllPlayer());
    }

    @GetMapping("/showbase")
    public Result<PlayerShowResp> showPlayer() {
        return Result.success(playerService.showOnePlayer());
    }

    @PostMapping("/create")
    public Result<PlayerCreateResp> createPlayer(@RequestBody PlayerClass playerClass) {
        PlayerCreateResp resp = playerService.createPlayer(playerClass);
        return Result.success(resp);
    }

    @PostMapping("/levelup")
    public Result<String> playerLevelUP(@RequestBody Long exp) {
        UserPlayerInfo userPlayerInfo = playerProviderService.getPlayerInfo();
        Long newExp = userPlayerInfo.getExp()+exp;
        userPlayerInfo.setExp(newExp);
        playerProviderService.updatePlayerInfo(userPlayerInfo);
        playerService.tryLevelUp();
        return Result.success("升级成功");
    }

    @PostMapping("/reset")
    public Result<String> playerReset(HttpServletRequest request) {
        Long userId = Long.parseLong(request.getAttribute("userId").toString());
        playerService.resetPlayer(userId);
        return Result.success("角色重置成功");
    }
}
