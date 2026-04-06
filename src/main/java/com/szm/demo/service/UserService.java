package com.szm.demo.service;

import com.szm.demo.dto.PlayerShowResp;
import com.szm.demo.dto.UserLoginReq;
import com.szm.demo.dto.UserRegisterReq;
import com.szm.demo.entity.UserDetail;

public interface UserService {
    void register(UserRegisterReq req);
    String login(UserLoginReq req);
    void logout(String token);
    UserDetail getUserDetail(Long userId);
    void setUserDetail(UserDetail userDetail);
    void setExp(Long userId, Long exp);
}
