package com.szm.demo.dto;

import com.szm.demo.entity.UserInfo;

import java.time.LocalDateTime;

public class UserRegisterReq {
    private String username;
    private String email;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public UserInfo toEntity(){
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(this.username);
        userInfo.setEmail(this.email);
        userInfo.setPassword(this.password);//todo:密码加密/不存储明文
        userInfo.setCreateTime(LocalDateTime.now());
        userInfo.setUpdateTime(LocalDateTime.now());
        return userInfo;
    }
}
