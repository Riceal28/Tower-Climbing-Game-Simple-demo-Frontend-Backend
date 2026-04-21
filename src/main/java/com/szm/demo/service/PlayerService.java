package com.szm.demo.service;

import com.szm.demo.common.PlayerClass;
import com.szm.demo.dto.PlayerShowResp;
import com.szm.demo.entity.ActionInfo;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;

public interface PlayerService {
    void createPlayer(PlayerClass playerClass);
    void updatePlayerBySave(SaveInfo saveInfo);
    PlayerShowResp showPlayer();
    void resetPlayer(Long userId);
    Long checkOverflowExp();
    void tryLevelUp();
    void afterActionByPlayer(UserPlayerInfo userPlayerInfo, ActionInfo actionInfo);
}
