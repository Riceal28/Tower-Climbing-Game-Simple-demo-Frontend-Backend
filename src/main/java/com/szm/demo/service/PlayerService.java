package com.szm.demo.service;

import com.szm.demo.dto.PlayerShowResp;

public interface PlayerService {
    void createDefaultPlayer(Long userId);
    PlayerShowResp showPlayer(Long userId);
    void resetPlayer(Long userId);
    Long checkOverflowExp(Long userId);
    void tryLevelUp(Long userId);
}
