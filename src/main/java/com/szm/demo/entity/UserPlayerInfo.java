package com.szm.demo.entity;

import com.szm.demo.common.PlayerClass;

import java.time.LocalDateTime;

/**
 * 用户角色详情实体类
 */
public class UserPlayerInfo {

    private Long id;
    private Long userId;
    private PlayerClass playerClass;
    private Integer level;
    private Long exp;
    private Integer attackBase;
    private Integer currentHp;
    private Integer currentMp;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public UserPlayerInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public Integer getAttackBase() {
        return attackBase;
    }

    public void setAttackBase(Integer attackBase) {
        this.attackBase = attackBase;
    }

    public Integer getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(Integer currentHp) {
        this.currentHp = currentHp;
    }

    public Integer getCurrentMp() {
        return currentMp;
    }

    public void setCurrentMp(Integer currentMp) {
        this.currentMp = currentMp;
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
    public String toString() {
        return String.format(
                "UserDetail[id=%s, userId=%s, playerClass=%s, level=%s, exp=%s, attackBase=%s, currentHp=%s, currentMp=%s, createTime=%s, updateTime=%s]",
                getId(), getUserId(), getPlayerClass(), getLevel(), getExp(), getAttackBase(), getCurrentHp(), getCurrentMp(), getCreateTime(), getUpdateTime()
        );
    }
}