package com.szm.demo.entity;

import java.time.LocalDateTime;

public class SaveInfo {
    private Long id;
    private Long userId;
    private Integer level;
    private Integer exp;
    private Integer floor;
    private Integer progress;
    private Long monsterId;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getExp() {
        return exp;
    }

    public void setExp(Integer exp) {
        this.exp = exp;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
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
                "SaveInfo[id=%s, userId=%s, level=%s, exp=%s, floor=%s, progress=%s, monsterId=%s, isActive=%s, createTime=%s, updateTime=%s]",
                getId(), getUserId(), getLevel(), getExp(), getFloor(), getProgress(),
                getMonsterId(), getIsActive(), getCreateTime(), getUpdateTime()
        );
    }
}