package com.szm.demo.entity;

import java.time.LocalDateTime;

/**
 * 用户详情表实体类
 */
public class UserDetail {

    private Long id;
    private Long userId;
    private Integer level;
    private Long exp;
    private Integer attackBase;
    private Integer currentHp;
    private Integer currentMp;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public UserDetail() {
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
                "UserDetail[id=%s, userId=%s, level=%s, exp=%s, attackBase=%s, currentHp=%s, currentMp=%s, createTime=%s, updateTime=%s]",
                getId(), getUserId(), getLevel(), getExp(), getAttackBase(),
                getCurrentHp(), getCurrentMp(), getCreateTime(), getUpdateTime()
        );
    }
}