package com.szm.demo.service;

import com.szm.demo.entity.SaveInfo;

public interface BattleService {
    //战斗信息初始化(读取存档中的进度,)
    void battleInit(SaveInfo saveInfo);
}
