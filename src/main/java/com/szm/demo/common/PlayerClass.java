package com.szm.demo.common;

public enum PlayerClass {
    SABER("SABER"),
    ARCHER("ARCHER"),
    CASTER("CASTER");

    private final String value;
    PlayerClass(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    @Override
    public String toString(){
        return value;
    }

    public static PlayerClass getByValue(String value) {
        for (PlayerClass playerClass : values()) {
            if (playerClass.getValue().equals(value)) {
                return playerClass;
            }
        }
        throw new BusinessException(ResultCode.BAD_REQUEST,"无效枚举");
    }
}
