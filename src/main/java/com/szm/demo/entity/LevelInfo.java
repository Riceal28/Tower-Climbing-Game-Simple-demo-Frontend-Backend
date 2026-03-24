package com.szm.demo.entity;

import java.time.LocalDateTime;

public class LevelInfo {
    private Integer id;
    private Integer level;
    private Long neededExp;
    private Integer maxHp;
    private Integer maxMp;
    private Integer attackBase;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public LevelInfo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getNeededExp() {
        return neededExp;
    }

    public void setNeededExp(Long neededExp) {
        this.neededExp = neededExp;
    }

    public Integer getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(Integer maxHp) {
        this.maxHp = maxHp;
    }

    public Integer getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(Integer maxMp) {
        this.maxMp = maxMp;
    }

    public Integer getAttackBase() {
        return attackBase;
    }

    public void setAttackBase(Integer attackBase) {
        this.attackBase = attackBase;
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
                "LevelInfo[id=%s, level=%s, neededExp=%s, maxHp=%s, maxMp=%s, attackBase=%s, createTime=%s, updateTime=%s]",
                getId(), getLevel(), getNeededExp(), getMaxHp(), getMaxMp(), getAttackBase(),
                getCreateTime(), getUpdateTime()
        );
    }
}