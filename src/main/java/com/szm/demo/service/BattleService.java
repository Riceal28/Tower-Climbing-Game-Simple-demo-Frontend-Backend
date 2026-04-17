package com.szm.demo.service;

import com.szm.demo.entity.BattleInfo;
import com.szm.demo.entity.SaveInfo;

public interface BattleService {
    BattleInfo getBySaveId();
    BattleInfo addBySave(SaveInfo saveInfo);
    BattleInfo loadFromSave(BattleInfo battleInfo, SaveInfo saveInfo);
    void updateByBattle(BattleInfo battleInfo);
    void battleInit();
}
