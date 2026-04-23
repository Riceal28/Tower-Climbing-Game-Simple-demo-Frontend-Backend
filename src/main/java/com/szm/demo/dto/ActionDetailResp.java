package com.szm.demo.dto;

public class ActionDetailResp {
    private Long actionId;
    private String actionType;
    private String actionName;
    private String description;
    private Boolean isTargetPlayer;
    private Integer forHp;
    private Integer forMp;
    private Integer forDefend;
    private Integer mpCost;
    private Integer currentCd;
    private Integer restContinueRound;

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

    public Boolean getTargetPlayer() {
        return isTargetPlayer;
    }

    public void setTargetPlayer(Boolean targetPlayer) {
        isTargetPlayer = targetPlayer;
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
}
