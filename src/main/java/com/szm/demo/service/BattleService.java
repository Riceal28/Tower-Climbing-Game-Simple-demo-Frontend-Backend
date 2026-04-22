package com.szm.demo.service;

import com.szm.demo.dto.BattleResp;
import com.szm.demo.entity.*;

public interface BattleService {

    BattleInfo create(SaveInfo saveInfo);

    BattleInfo getBySaveId();

    BattleInfo convertFromSave(SaveInfo saveInfo);

    void afterOneAction(BattleInfo battleInfo, ActionInfo actionInfo);
    /**
     * 开始新战斗：从存档创建战斗实例，绑定楼层怪物与双方技能
     */
    BattleResp startBattle();

    /**
     * 玩家使用技能
     */
    BattleResp playerAction(Long actionId);

    /**
     * 结束回合：魔物行动 + 回合结束处理(CD/持续效果)
     */
    BattleResp endRound();

    /**
     * 查询当前战斗状态
     */
    BattleResp getStatus();

    /**
     * 内部：检查胜负，返回 null 表示战斗继续，非null表示结果("WIN"/"LOSE")
     */
    String checkBattleEnd(BattleInfo battleInfo);


}
