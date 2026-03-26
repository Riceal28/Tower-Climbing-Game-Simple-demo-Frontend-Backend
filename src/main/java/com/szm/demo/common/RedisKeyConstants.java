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
    USER_INFO("demo:user:info:%s"),
    USER_LOGIN("demo:user:login:%s"),
    USER_DETAIL("demo:user:detail:%s"),
    USER_TOKEN_IN("demo:user:token:%s"),
    USER_TOKEN_OUT("demo:user:token:black:%s");

    private final String key;

    RedisKeyConstants(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public String getKey(Object... args) {
        return String.format(key, args);
    }
}
