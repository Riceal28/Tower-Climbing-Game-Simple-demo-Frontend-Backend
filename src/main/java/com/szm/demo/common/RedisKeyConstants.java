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
    USER_DETAIL("demo:user:detail:%s","userId"),
    USER_TOKEN_IN("demo:user:token:%s","JTI"),
    USER_TOKEN_OUT("demo:user:token:blacklist:%s","JTI"),
    LEVEL_INFO("demo:level:%s","level"),
    PLAYER_SHOW("demo:user:player:%s","userId"),
    SAVE_LIST("demo:save:list:%s","userId"),
    SAVE_DETAIL("demo:save:%s:%s","userId, id");

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
