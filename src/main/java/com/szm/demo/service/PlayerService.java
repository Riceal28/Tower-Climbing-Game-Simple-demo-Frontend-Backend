package com.szm.demo.service;

import com.szm.demo.common.PlayerClass;
import com.szm.demo.dto.PlayerCreateResp;
import com.szm.demo.dto.PlayerShowResp;
import com.szm.demo.dto.SaveLoadResp;
import com.szm.demo.entity.ActionInfo;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;

import java.util.List;

public interface PlayerService {
    PlayerCreateResp createPlayer(PlayerClass playerClass);
    SaveLoadResp updatePlayerBySave(SaveInfo saveInfo);
    PlayerShowResp showOnePlayer();
    List<PlayerShowResp> showAllPlayer();
    void resetPlayer(Long userId);
    Long checkOverflowExp();
    void tryLevelUp();
    void afterActionByPlayer(UserPlayerInfo userPlayerInfo, ActionInfo actionInfo);
}
