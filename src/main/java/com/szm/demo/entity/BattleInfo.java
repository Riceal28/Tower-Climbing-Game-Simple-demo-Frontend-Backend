package com.szm.demo.entity;

import java.time.LocalDateTime;

public class BattleInfo {
    private Long id;
    private Long saveId;
    private Long monsterId;
    private Integer playerCurrentHp;
    private Integer playerCurrentMp;
    private Integer playerCurrentDefend;
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

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public Integer getPlayerCurrentHp() {
        return playerCurrentHp;
    }

    public void setPlayerCurrentHp(Integer playerCurrentHp) {
        this.playerCurrentHp = playerCurrentHp;
    }

    public Integer getPlayerCurrentMp() {
        return playerCurrentMp;
    }

    public void setPlayerCurrentMp(Integer playerCurrentMp) {
        this.playerCurrentMp = playerCurrentMp;
    }

    public Integer getPlayerCurrentDefend() {
        return playerCurrentDefend;
    }

    public void setPlayerCurrentDefend(Integer playerCurrentDefend) {
        this.playerCurrentDefend = playerCurrentDefend;
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
                "BattleInfo[id=%s, saveId=%s, monsterId=%s, playerCurrentHp=%s, playerCurrentMp=%s, playerCurrentDefend=%s, monsterCurrentHp=%s, monsterCurrentMp=%s, monsterCurrentDefend=%s, createTime=%s, updateTime=%s]",
                getId(), getSaveId(), getMonsterId(), getPlayerCurrentHp(), getPlayerCurrentMp(), getPlayerCurrentDefend(),
                getMonsterCurrentHp(), getMonsterCurrentMp(), getMonsterCurrentDefend(), getCreateTime(), getUpdateTime()
        );
    }
}