package com.szm.demo.entity;

import java.time.LocalDateTime;

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
    private Boolean isActive;
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

    public Integer getCurrentMp() {
        return currentMp;
    }

    public void setCurrentMp(Integer currentMp) {
        this.currentMp = currentMp;
    }

    public Integer getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(Integer currentHp) {
        this.currentHp = currentHp;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
                "SaveInfo[id=%s, userId=%s, playerId, level=%s, exp=%s, currentHp=%s, currentMp=%s, floor=%s, " +
                        "battleOrder=%s, progress=%s, isActive=%s, createTime=%s, " +
                        "updateTime=%s]",
                getId(), getUserId(), getPlayerId(), getLevel(), getExp(), getCurrentHp(), getCurrentMp(), getFloor(),
                getBattleOrder(), getProgress(), getIsActive(), getCreateTime(), getUpdateTime()
        );
    }
}