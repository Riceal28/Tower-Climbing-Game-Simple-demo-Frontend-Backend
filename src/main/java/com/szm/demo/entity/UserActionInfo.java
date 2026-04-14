package com.szm.demo.entity;

import java.time.LocalDateTime;

public class UserActionInfo {
    private Long id;
    private Long battleId;
    private Long userId;
    private Long actionId;
    private Integer currentCd;
    private Integer restContinueRound;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public UserActionInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBattleId() {
        return battleId;
    }

    public void setBattleId(Long battleId) {
        this.battleId = battleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public Integer getCurrentCd() {
        return currentCd;
    }

    public void setCurrentCd(Integer currentCd) {
        this.currentCd = currentCd;
    }

    public Integer getRestContinueRound() {
        return restContinueRound;
    }

    public void setRestContinueRound(Integer restContinueRound) {
        this.restContinueRound = restContinueRound;
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
        return String.format("UserActionInfo[id=%s, battleId=%s, userId=%s, actionId=%s, currentCd=%s, restContinueRound=%s, createTime=%s, updateTime=%s]",
                getId(), getBattleId(), getUserId(), getActionId(), getCurrentCd(), getRestContinueRound(), getCreateTime(), getUpdateTime());
    }
}