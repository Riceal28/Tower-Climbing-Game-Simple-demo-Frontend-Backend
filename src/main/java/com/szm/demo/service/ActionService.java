package com.szm.demo.service;

import com.szm.demo.entity.ActionInfo;
import com.szm.demo.entity.MonsterActionInfo;
import com.szm.demo.entity.PlayerActionInfo;

import java.util.List;

public interface ActionService {
    void addDefaultAction(Long playerId);
    ActionInfo getActionByAId(Long actionId);
    PlayerActionInfo getPaById(Long id);
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
