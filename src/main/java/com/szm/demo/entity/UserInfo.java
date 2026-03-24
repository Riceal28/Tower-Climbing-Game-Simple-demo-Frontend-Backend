package com.szm.demo.entity;

import java.time.LocalDateTime;

public class UserInfo {
    private Long id;
    private String email;
    private String username;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public UserInfo(){
    }

    public Long getId(){
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    @Override
    public String toString(){
        return String.format("UserInfo[id=%s, email=%s, username=%s, createTime=%s, updateTime=%s]",
                getId(),getEmail(),getUsername(),getCreateTime(),getUpdateTime());
    }
}
