package com.szm.demo.service;

import com.szm.demo.common.PlayerClass;
import com.szm.demo.dto.PlayerShowResp;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;

public interface PlayerService {
    void createPlayer(PlayerClass playerClass);
    UserPlayerInfo getPlayerInfo();
    void updatePlayerInfo(UserPlayerInfo userPlayerInfo);
    void updatePlayerBySave(SaveInfo saveInfo);
    PlayerShowResp showPlayer();
    void resetPlayer(Long userId);
    Long checkOverflowExp();
    void tryLevelUp();
}
