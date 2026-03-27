package com.szm.demo.service;

import com.szm.demo.entity.LevelInfo;

public interface LevelService {
    LevelInfo getLevelInfo(Integer level);
    Long levelUp(Long userId, Long overflowExp);
}
