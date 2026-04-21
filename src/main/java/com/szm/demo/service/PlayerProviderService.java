package com.szm.demo.service;

import com.szm.demo.entity.UserPlayerInfo;

public interface PlayerProviderService {

    UserPlayerInfo getPlayerInfo();

    void updatePlayerInfo(UserPlayerInfo userPlayerInfo);
}
