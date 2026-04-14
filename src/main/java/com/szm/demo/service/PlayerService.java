package com.szm.demo.service;

import com.szm.demo.common.PlayerClass;
import com.szm.demo.dto.PlayerShowResp;

public interface PlayerService {
    void createPlayer(Long userId, PlayerClass playerClass);
    PlayerShowResp showPlayer(Long userId);
    void resetPlayer(Long userId);
    Long checkOverflowExp(Long userId);
    void tryLevelUp(Long userId);
}
