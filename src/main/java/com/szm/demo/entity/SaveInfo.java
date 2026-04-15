package com.szm.demo.entity;

import java.time.LocalDateTime;

/**
 * 游戏存档表 实体类
 * 对应数据库表：save_info
 */
public class SaveInfo {
    private Long id;
    private Long userId;
    private Long playerId;
    private Integer level;
    private Long exp;
    private Integer currentHp;
    private Integer currentMp;
    private Integer floor;
    private Integer battleOrder;
    private Integer progress;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public SaveInfo() {
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

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
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

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getBattleOrder() {
        return battleOrder;
    }

    public void setBattleOrder(Integer battleOrder) {
        this.battleOrder = battleOrder;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
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

    /**
     * 修复toString方法格式错误、字段缺失问题
     */
    @Override
    public String toString() {
        return String.format("SaveInfo[id=%s, userId=%s, playerId=%s, level=%s, exp=%s, currentHp=%s, currentMp=%s, floor=%s, battleOrder=%s, progress=%s, createTime=%s, updateTime=%s]",
                getId(), getUserId(), getPlayerId(), getLevel(), getExp(), getCurrentHp(), getCurrentMp(), getFloor(), getBattleOrder(), getProgress(), getCreateTime(), getUpdateTime());
    }
}