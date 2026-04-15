package com.szm.demo.context;

public class GameContext implements AutoCloseable{
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> PLAYER_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> SAVE_ID = new ThreadLocal<>();

    public static void init(Long userId, Long playerId, Long saveId){
        USER_ID.set(userId);
        PLAYER_ID.set(playerId);
        SAVE_ID.set(saveId);
    }

    public static Long getUserId(){
        return USER_ID.get();
    }

    public static Long getPlayerId(){
        return PLAYER_ID.get();
    }

    public static Long getSaveId(){
        return SAVE_ID.get();
    }

    @Override
    public void close() {
        clear();
    }

    public static void clear(){
        USER_ID.remove();
        PLAYER_ID.remove();
        SAVE_ID.remove();
    }
}
