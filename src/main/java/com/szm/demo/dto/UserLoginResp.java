package com.szm.demo.dto;

public class UserLoginResp {
    private String token;
    
    public UserLoginResp() {
    }

    public UserLoginResp(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
