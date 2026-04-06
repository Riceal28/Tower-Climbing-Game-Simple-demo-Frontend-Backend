package com.szm.demo.dto;

public class PlayerShowResp {
    private Integer level;
    private Long exp;
    private Integer attackBase;
    private Integer maxHp;
    private Integer currentHp;
    private Integer maxMp;
    private Integer currentMp;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public Integer getAttackBase() {
        return attackBase;
    }

    public void setAttackBase(Integer attackBase) {
        this.attackBase = attackBase;
    }

    public Integer getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(Integer maxHp) {
        this.maxHp = maxHp;
    }

    public Integer getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(Integer currentHp) {
        this.currentHp = currentHp;
    }

    public Integer getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(Integer maxMp) {
        this.maxMp = maxMp;
    }

    public Integer getCurrentMp() {
        return currentMp;
    }

    public void setCurrentMp(Integer currentMp) {
        this.currentMp = currentMp;
    }
}
