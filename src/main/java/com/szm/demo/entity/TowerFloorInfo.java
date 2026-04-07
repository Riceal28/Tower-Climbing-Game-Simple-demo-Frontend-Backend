package com.szm.demo.entity;

import java.time.LocalDateTime;

public class TowerFloorInfo {
    private Long id;
    private Integer floor;          // 层数
    private Integer progressNeeded; // 下一层所需进度
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public TowerFloorInfo() {
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

    public Integer getProgressNeeded() {
        return progressNeeded;
    }

    public void setProgressNeeded(Integer progressNeeded) {
        this.progressNeeded = progressNeeded;
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
        return String.format("TowerFloorInfo[id=%s, floor=%s, progressNeeded=%s, createTime=%s, updateTime=%s]",
                getId(), getFloor(), getProgressNeeded(), getCreateTime(), getUpdateTime());
    }
}