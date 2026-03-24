package com.szm.demo.entity;

import java.time.LocalDateTime;

public class ActionInfo {
    private Long id;
    private Long actionId;
    private String actionType;
    private String actionName;
    private String description;
    private Boolean targetIsForSelf;
    private Integer forHp;
    private Integer forMp;
    private Integer forDefend;
    private Integer mpCost;
    private Boolean isContinue;
    private Integer continueRound;
    private Integer cd;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public ActionInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getTargetIsForSelf() {
        return targetIsForSelf;
    }

    public void setTargetIsForSelf(Boolean targetIsForSelf) {
        this.targetIsForSelf = targetIsForSelf;
    }

    public Integer getForHp() {
        return forHp;
    }

    public void setForHp(Integer forHp) {
        this.forHp = forHp;
    }

    public Integer getForMp() {
        return forMp;
    }

    public void setForMp(Integer forMp) {
        this.forMp = forMp;
    }

    public Integer getForDefend() {
        return forDefend;
    }

    public void setForDefend(Integer forDefend) {
        this.forDefend = forDefend;
    }

    public Integer getMpCost() {
        return mpCost;
    }

    public void setMpCost(Integer mpCost) {
        this.mpCost = mpCost;
    }

    public Boolean getIsContinue() {
        return isContinue;
    }

    public void setIsContinue(Boolean isContinue) {
        this.isContinue = isContinue;
    }

    public Integer getContinueRound() {
        return continueRound;
    }

    public void setContinueRound(Integer continueRound) {
        this.continueRound = continueRound;
    }

    public Integer getCd() {
        return cd;
    }

    public void setCd(Integer cd) {
        this.cd = cd;
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
                "ActionInfo[id=%s, actionId=%s, actionType=%s, actionName=%s, description=%s, targetIsForSelf=%s, forHp=%s, forMp=%s, forDefend=%s, mpCost=%s, isContinue=%s, continueRound=%s, cd=%s, createTime=%s, updateTime=%s]",
                getId(), getActionId(), getActionType(), getActionName(), getDescription(), getTargetIsForSelf(),
                getForHp(), getForMp(), getForDefend(), getMpCost(), getIsContinue(), getContinueRound(),
                getCd(), getCreateTime(), getUpdateTime()
        );
    }
}