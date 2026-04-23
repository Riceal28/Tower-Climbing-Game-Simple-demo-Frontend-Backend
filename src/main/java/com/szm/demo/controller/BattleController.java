package com.szm.demo.controller;

import com.szm.demo.common.ApiConstant;
import com.szm.demo.common.Result;
import com.szm.demo.dto.ActionDetailResp;
import com.szm.demo.dto.BattleResp;
import com.szm.demo.service.ActionService;
import com.szm.demo.service.BattleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstant.API_BATTLE)
public class BattleController {

    @Autowired
    BattleService battleService;
    @Autowired
    private ActionService actionService;

    @PostMapping("/start")
    public Result<BattleResp> start() {
        return Result.success(battleService.startBattle());
    }

    @GetMapping("/status")
    public Result<BattleResp> status() {
        return Result.success(battleService.getStatus());
    }
    @GetMapping("/actionList")
    public Result<List<ActionDetailResp>> getActions(){
        List<ActionDetailResp> resp = actionService.getAllActionResp();
        return Result.success(resp);
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
