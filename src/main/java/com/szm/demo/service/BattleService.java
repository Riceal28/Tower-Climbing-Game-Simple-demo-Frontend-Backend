package com.szm.demo.service;

import com.szm.demo.entity.*;

public interface BattleService {

    BattleInfo create(SaveInfo saveInfo);

    BattleInfo getBySaveId();

    BattleInfo convertFromSave(SaveInfo saveInfo);

    void updateBattle(BattleInfo battleInfo);

    void afterOneAction(BattleInfo battleInfo, ActionInfo actionInfo);
}
