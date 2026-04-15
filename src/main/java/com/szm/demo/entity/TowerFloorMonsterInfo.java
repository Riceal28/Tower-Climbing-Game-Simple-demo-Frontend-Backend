package com.szm.demo.entity;

import java.time.LocalDateTime;

public class TowerFloorMonsterInfo {
    private Long id;
    private Integer floor;          // 层数
    private Integer battleOrder;   // 遇敌顺序
    private Long monsterId;        // 魔物ID
    private Integer rewardProgress;// 击败后获得的层数进度
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public TowerFloorMonsterInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public Integer getBattleOrder() {
        return battleOrder;
    }

    public void setBattleOrder(Integer battleOrder) {
        this.battleOrder = battleOrder;
    }

    public Integer getRewardProgress() {
        return rewardProgress;
    }

    public void setRewardProgress(Integer rewardProgress) {
        this.rewardProgress = rewardProgress;
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
        return String.format("TowerFloorMonsterInfo[id=%s, floor=%s, monsterId=%s, battleOrder=%s, rewardProgress=%s, createTime=%s, updateTime=%s]",
                getId(), getFloor(), getMonsterId(), getBattleOrder(), getRewardProgress(), getCreateTime(), getUpdateTime());
    }
}