package com.szm.demo.entity;

import java.time.LocalDateTime;

public class BattleInfo {
    private Long id;
    private Long saveId;
    private Long userId;
    private Long monsterId;
    private Integer userCurrentHp;
    private Integer userCurrentMp;
    private Integer userCurrentDefend;
    private Integer monsterCurrentHp;
    private Integer monsterCurrentMp;
    private Integer monsterCurrentDefend;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public BattleInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSaveId() {
        return saveId;
    }

    public void setSaveId(Long saveId) {
        this.saveId = saveId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public Integer getUserCurrentHp() {
        return userCurrentHp;
    }

    public void setUserCurrentHp(Integer userCurrentHp) {
        this.userCurrentHp = userCurrentHp;
    }

    public Integer getUserCurrentMp() {
        return userCurrentMp;
    }

    public void setUserCurrentMp(Integer userCurrentMp) {
        this.userCurrentMp = userCurrentMp;
    }

    public Integer getUserCurrentDefend() {
        return userCurrentDefend;
    }

    public void setUserCurrentDefend(Integer userCurrentDefend) {
        this.userCurrentDefend = userCurrentDefend;
    }

    public Integer getMonsterCurrentHp() {
        return monsterCurrentHp;
    }

    public void setMonsterCurrentHp(Integer monsterCurrentHp) {
        this.monsterCurrentHp = monsterCurrentHp;
    }

    public Integer getMonsterCurrentMp() {
        return monsterCurrentMp;
    }

    public void setMonsterCurrentMp(Integer monsterCurrentMp) {
        this.monsterCurrentMp = monsterCurrentMp;
    }

    public Integer getMonsterCurrentDefend() {
        return monsterCurrentDefend;
    }

    public void setMonsterCurrentDefend(Integer monsterCurrentDefend) {
        this.monsterCurrentDefend = monsterCurrentDefend;
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
                "BattleInfo[id=%s, saveId=%s, userId=%s, monsterId=%s, userCurrentHp=%s, userCurrentMp=%s, userCurrentDefend=%s, monsterCurrentHp=%s, monsterCurrentMp=%s, monsterCurrentDefend=%s, createTime=%s, updateTime=%s]",
                getId(), getSaveId(), getUserId(), getMonsterId(), getUserCurrentHp(), getUserCurrentMp(), getUserCurrentDefend(),
                getMonsterCurrentHp(), getMonsterCurrentMp(), getMonsterCurrentDefend(), getCreateTime(), getUpdateTime()
        );
    }
}