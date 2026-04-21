package com.szm.demo.service;

import com.szm.demo.entity.MonsterActionInfo;
import com.szm.demo.entity.MonsterInfo;

import java.util.List;

public interface MonsterService {
    MonsterInfo getByMId(Long monsterId);
    List<MonsterActionInfo> getMonsterActions(Long monsterId);
}
