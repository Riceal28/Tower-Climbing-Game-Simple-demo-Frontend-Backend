package com.szm.demo.controller;

import com.szm.demo.common.ApiConstant;
import com.szm.demo.common.Result;
import com.szm.demo.dto.BattleResp;
import com.szm.demo.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiConstant.API_BATTLE)
public class BattleController {

    @Autowired
    BattleService battleService;

    @PostMapping("/start")
    public Result<BattleResp> start() {
        return Result.success(battleService.startBattle());
    }

    @GetMapping("/status")
    public Result<BattleResp> status() {
        return Result.success(battleService.getStatus());
    }

    @PostMapping("/action")
    public Result<BattleResp> action(@RequestBody Long actionId) {
        return Result.success(battleService.playerAction(actionId));
    }

    @PostMapping("/round-end")
    public Result<BattleResp> roundEnd() {
        return Result.success(battleService.endRound());
    }
}
