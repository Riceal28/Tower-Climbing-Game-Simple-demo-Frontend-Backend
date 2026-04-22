package com.szm.demo.service;

import com.szm.demo.entity.UserPlayerInfo;

import java.util.List;

public interface PlayerProviderService {

    UserPlayerInfo getPlayerInfo();

    List<UserPlayerInfo> getPlayerInfoByUserId();

    void updatePlayerInfo(UserPlayerInfo userPlayerInfo);
}
