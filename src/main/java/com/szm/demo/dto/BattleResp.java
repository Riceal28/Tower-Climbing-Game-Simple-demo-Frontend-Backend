package com.szm.demo.dto;

import com.szm.demo.entity.BattleInfo;
import com.szm.demo.entity.MonsterInfo;

/**
 * 战斗统一响应：状态快照 + 流水信息 + 结果
 */
public class BattleResp {
    private BattleInfo battleInfo;       // 战斗实时状态
    private MonsterInfo monsterInfo;     // 魔物基础信息（供前端展示名字等）
    private String log;                  // 本次操作流水日志（如"使用了XX，造成X点伤害"）
    private String result;               // 胜负结果：null=进行中 / "WIN" / "LOSE"

    public BattleResp() {
    }

    public BattleResp(BattleInfo battleInfo, MonsterInfo monsterInfo, String log, String result) {
        this.battleInfo = battleInfo;
        this.monsterInfo = monsterInfo;
        this.log = log;
        this.result = result;
    }

    public BattleInfo getBattleInfo() {
        return battleInfo;
    }

    public void setBattleInfo(BattleInfo battleInfo) {
        this.battleInfo = battleInfo;
    }

    public MonsterInfo getMonsterInfo() {
        return monsterInfo;
    }

    public void setMonsterInfo(MonsterInfo monsterInfo) {
        this.monsterInfo = monsterInfo;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
