package com.szm.demo.service;

import com.szm.demo.entity.BattleInfo;
import com.szm.demo.entity.MonsterInfo;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;

public interface BattleService {

    BattleInfo create(SaveInfo saveInfo);

    BattleInfo getBySaveId();

    BattleInfo convertFromSave(SaveInfo saveInfo);

    void updateBattle(BattleInfo battleInfo);
}
