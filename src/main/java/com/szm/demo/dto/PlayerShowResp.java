package com.szm.demo.dto;

import com.szm.demo.common.PlayerClass;

public class PlayerShowResp {
    private PlayerClass playerClass;
    private Integer level;
    private Long exp;
    private Integer attackBase;
    private Integer maxHp;
    private Integer maxMp;

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

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

    public Integer getMaxMp() {
        return maxMp;
    }

    public void setMaxMp(Integer maxMp) {
        this.maxMp = maxMp;
    }

}
