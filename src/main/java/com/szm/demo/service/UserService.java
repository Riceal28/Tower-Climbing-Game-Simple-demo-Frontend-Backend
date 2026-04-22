package com.szm.demo.service;

import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserLoginResp;
import com.szm.demo.dto.UserRegisterReq;
import com.szm.demo.entity.UserPlayerInfo;

public interface UserService {
    void register(UserRegisterReq req);
    UserLoginResp login(UserLoginReq req);
    void logout(String token);
}
