package com.szm.demo.service;

public interface PlayerService {
    void createDefaultPlayer(Long userId);
    void resetPlayer(Long userId);
    Long checkOverflowExp(Long userId);
    void tryLevelUp(Long userId);
}
