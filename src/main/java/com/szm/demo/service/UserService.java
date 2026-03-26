package com.szm.demo.service;

import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserRegisterReq;

public interface UserService {
    void register(UserRegisterReq req);
    String login(UserLoginReq req);
    void logout(String token);
    void createDefaultPlayer(Long userId);
}
