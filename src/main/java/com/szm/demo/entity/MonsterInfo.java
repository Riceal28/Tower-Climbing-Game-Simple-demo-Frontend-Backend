package com.szm.demo.entity;

import java.time.LocalDateTime;

public class MonsterInfo {
    private Long id;
    private Long monsterId;
    private String monsterName;
    private String description;
    private Integer hp;
    private Integer mp;
    private Integer attackBase;
    private Long gainExp;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public MonsterInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(Long monsterId) {
        this.monsterId = monsterId;
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public Integer getAttackBase() {
        return attackBase;
    }

    public void setAttackBase(Integer attackBase) {
        this.attackBase = attackBase;
    }

    public Long getGainExp() {
        return gainExp;
    }

    public void setGainExp(Long gainExp) {
        this.gainExp = gainExp;
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
        return String.format("MonsterInfo[id=%s, monsterId=%s, monsterName=%s, description=%s, hp=%s, mp=%s, attackBase=%s, gainExp=%s, createTime=%s, updateTime=%s]",
                getId(), getMonsterId(), getMonsterName(), getDescription(), getHp(), getMp(), getAttackBase(), getGainExp(), getCreateTime(), getUpdateTime());
    }
}