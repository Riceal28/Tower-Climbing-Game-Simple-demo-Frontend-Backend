package com.szm.demo.service;

import com.szm.demo.entity.TowerFloorInfo;
import com.szm.demo.entity.TowerFloorMonsterInfo;

import java.util.List;

public interface TowerService {

    TowerFloorInfo getBaseByFloor(Integer floor);
    List<TowerFloorMonsterInfo> getDetailByFloor(Integer floor);
    TowerFloorMonsterInfo getOneDetailByOrder(Integer floor, Integer battleOrder);
    Boolean hasNextBattle(Integer currentFloor, Integer currentOrder);
    Boolean hasNextFloor(Integer currentFloor);
}
