package com.szm.demo.dto;

public class UserLoginResp {
    private String token;
    private Long userId;

    public UserLoginResp() {
    }

    public UserLoginResp(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
