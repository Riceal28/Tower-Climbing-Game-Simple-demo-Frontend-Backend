package com.szm.demo.common;

public enum RedisKeyConstants {

    //    USER_INFO_KEY("userInfo:"),
//    USER_DETAIL_KEY("userDetail:"),
//    LEVEL_INFO("levelInfo:"),
//    SAVE_INFO_KEY("saveInfo:"),
//    ACTION_INFO("actionInfo:"),
//    USER_ACTION_INFO_KEY("userActionInfo:"),
//    MONSTER_INFO("monsterInfo:"),
//    MONSTER_ACTION_INFO("monsterActionInfo:"),
//    BATTLE_INFO("battleInfo:");
    USER_INFO("demo:user:info:%s","userId"),
    USER_PLAYER("demo:user:player:%s","playerId"),//todo:改为HASH
    USER_TOKEN_IN("demo:login:white:%s","JTI"),
    USER_TOKEN_OUT("demo:login:black:%s","JTI"),
    LEVEL_INFO("demo:level:class:%s:s","playerClass, level"),
    PLAYER_SHOW("demo:user:player:%s","userId"),
    SAVE_LIST("demo:save:list:%s","userId(存存档ID)"),
    SAVE_DETAIL("demo:save:%s:%s","userId, id"),
    TOWER_BASE("demo:tower:base:%s","floor"),
    TOWER_LIST("demo:tower:list:%s","floor(存order)"),
    TOWER_DETAIL("demo:tower:%s:%s","floor, order"),
    MONSTER_INFO("demo:monster:info:%s","monsterId");

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
