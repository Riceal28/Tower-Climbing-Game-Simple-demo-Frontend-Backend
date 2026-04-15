package com.szm.demo.service;

import com.szm.demo.common.PlayerClass;
import com.szm.demo.entity.LevelInfo;

public interface LevelService {
    LevelInfo getLevelInfo(PlayerClass playerClass, Integer level);

    Long levelUp(Long overflowExp);
}
