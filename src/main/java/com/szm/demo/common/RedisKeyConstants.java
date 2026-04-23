package com.szm.demo.common;

public enum RedisKeyConstants {

    //todo:简化Key
    USER_INFO("demo:user:info:%s","userId"),
    USER_PLAYER("demo:player:%s","playerId"),
    PLAYER_SHOW("demo:player:base:%s","playerId"),
    USER_TOKEN_IN("demo:login:white:%s","JTI"),
    USER_TOKEN_OUT("demo:login:black:%s","JTI"),
    LEVEL_INFO("demo:level:class:%s:%s","playerClass, level"),
    SAVE_LIST("demo:save:list:%s","userId(存存档ID)"),
    SAVE_DETAIL("demo:save:%s:%s","userId, saveId"),
    ACTION_INFO("demo:action:%s","actionId"),
    PLAYER_ACTION("demo:player:action:%s","battleId"),
    MONSTER_ACTION("demo:monster:action:%s","battleId"),
    TOWER_BASE("demo:tower:base:%s","floor"),
    TOWER_LIST("demo:tower:list:%s","floor(存order)"),
    TOWER_DETAIL("demo:tower:%s:%s","floor, order"),
    MONSTER_INFO("demo:monster:%s","monsterId"),
    BATTLE_INFO("demo:battle:%s","saveId");

    private final String key;
    private final String useBy;

    RedisKeyConstants(String key, String description) {
        this.key = key;
        this.useBy = description;
    }

    public String getKey() {
        return this.key;
    }

    public String getUseBy() {
        return useBy;
    }

    public String getKey(Object... args) {
        return String.format(key, args);
    }
}
