package com.szm.demo.service;

import com.szm.demo.dto.ActionDetailResp;
import com.szm.demo.entity.ActionInfo;
import com.szm.demo.entity.MonsterActionInfo;
import com.szm.demo.entity.PlayerActionInfo;

import java.util.List;

public interface ActionService {
    void addDefaultAction(Long playerId,Integer levelId);
    ActionInfo getActionByAId(Long actionId);
    PlayerActionInfo getPaById(Long id);
    List<PlayerActionInfo> getPaByBId(Long battleId);
    List<ActionDetailResp> getAllActionResp();
    void updatePaOne(PlayerActionInfo playerActionInfo);
    void updatePaAll(List<PlayerActionInfo> list);
    void passRoundOnePaUpdate(PlayerActionInfo playerActionInfo);
    void passRoundAllPaUpdate(List<PlayerActionInfo> list);

    MonsterActionInfo getMaById(Long id);
    void updateMaOne(MonsterActionInfo monsterActionInfo);
    void updateMaAll(List<MonsterActionInfo> list);
    void passRoundOneMaUpdate(MonsterActionInfo monsterActionInfo);
    void passRoundAllMaUpdate(List<MonsterActionInfo> list);
}
