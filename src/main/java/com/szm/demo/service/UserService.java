package com.szm.demo.service;

import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserRegisterReq;
import com.szm.demo.entity.UserInfo;

public interface UserService {
    void register(UserRegisterReq req);
    String login(UserLoginReq req);
}
