package com.szm.demo.service;

import com.szm.demo.entity.BattleInfo;
import com.szm.demo.entity.SaveInfo;

import java.util.List;

public interface SaveService {

    void createDefaultSave();
    List<SaveInfo> getSaveByUserId();
    List<SaveInfo> getSaveByPlayerId();
    List<SaveInfo> getSaveByPlayerId(Long playerId);
    SaveInfo getSaveById();

    /**
     * 保存当前战斗胜利后的存档
     * @param battleInfo 战斗信息
     */
    void saveAfterWin(BattleInfo battleInfo);
}
