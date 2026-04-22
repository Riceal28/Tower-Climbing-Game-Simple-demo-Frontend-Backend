package com.szm.demo.util;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.PlayerClass;
import com.szm.demo.common.ResultCode;
import com.szm.demo.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapUtil {

    private static final Logger logger = LoggerFactory.getLogger(MapUtil.class);

    private static LocalDateTime convertToTime(Object timeObj) {
        // 1. 空值直接返回 null
        if (timeObj == null) {
            return null;
        }
        // 2. 如果已经是 LocalDateTime，直接返回
        if (timeObj instanceof LocalDateTime) {
            return (LocalDateTime) timeObj;
        }
        try {
            String timeStr = timeObj.toString().trim();
            // 3. 处理数组格式：[2026, 4, 22, 11, 6, 28, 733880500]
            if (timeStr.startsWith("[") && timeStr.endsWith("]")) {
                List<Integer> parts = Arrays.stream(
                                timeStr.substring(1, timeStr.length() - 1).split(",")
                        )
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                // 数组格式 → 组装成 LocalDateTime
                return LocalDateTime.of(
                        parts.get(0),    // 年
                        parts.get(1),    // 月
                        parts.get(2),    // 日
                        parts.get(3),    // 时
                        parts.get(4),    // 分
                        parts.get(5),    // 秒
                        parts.size() > 6 ? parts.get(6) : 0 // 纳秒（可选）
                );
            }
            // 4. 标准字符串格式（兼容绝大多数情况）
            return LocalDateTime.parse(timeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            return null;
        }
    }
    public static UserPlayerInfo mapToPlayer(Map<String, Object> map) {
        if (map.isEmpty()) {
            logger.error("Map转换角色对象异常:空Map");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        UserPlayerInfo userPlayerInfo = new UserPlayerInfo();
        userPlayerInfo.setId(((Number) map.get("id")).longValue());
        userPlayerInfo.setUserId(((Number) map.get("userId")).longValue());
        userPlayerInfo.setPlayerClass(PlayerClass.getByValue((String) map.get("playerClass")));
        userPlayerInfo.setLevel(((Number) map.get("level")).intValue());
        userPlayerInfo.setExp(((Number) map.get("exp")).longValue());
        userPlayerInfo.setAttackBase(((Number) map.get("attackBase")).intValue());
        userPlayerInfo.setCurrentHp(((Number) map.get("currentHp")).intValue());
        userPlayerInfo.setCurrentMp(((Number) map.get("currentMp")).intValue());
        userPlayerInfo.setCreateTime(convertToTime(map.get("createTime")));
        userPlayerInfo.setUpdateTime(convertToTime(map.get("updateTime")));
        return userPlayerInfo;
    }

    public static Map<String, Object> playerToMap(UserPlayerInfo userPlayerInfo) {
        if (userPlayerInfo == null) {
            logger.error("角色对象映射Map异常: 空对象");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", userPlayerInfo.getId());
        map.put("userId", userPlayerInfo.getUserId());
        map.put("playerClass", userPlayerInfo.getPlayerClass());
        map.put("level", userPlayerInfo.getLevel());
        map.put("exp", userPlayerInfo.getExp());
        map.put("attackBase", userPlayerInfo.getAttackBase());
        map.put("currentHp", userPlayerInfo.getCurrentHp());
        map.put("currentMp", userPlayerInfo.getCurrentMp());
        map.put("createTime", userPlayerInfo.getCreateTime());
        map.put("updateTime", userPlayerInfo.getUpdateTime());
        return map;
    }

    public static SaveInfo mapToSaveInfo(Map<String, Object> map) {
        if (map.isEmpty()) {
            logger.error("Map转换存档对象异常:空Map");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        SaveInfo saveInfo = new SaveInfo();
        saveInfo.setId(((Number) map.get("id")).longValue());
        saveInfo.setUserId(((Number) map.get("userId")).longValue());
        saveInfo.setPlayerId(((Number) map.get("playerId")).longValue());
        saveInfo.setLevel(((Number) map.get("level")).intValue());
        saveInfo.setExp(((Number) map.get("exp")).longValue());
        saveInfo.setCurrentHp(((Number) map.get("currentHp")).intValue());
        saveInfo.setCurrentMp(((Number) map.get("currentMp")).intValue());
        saveInfo.setFloor(((Number) map.get("floor")).intValue());
        saveInfo.setBattleOrder(((Number) map.get("battleOrder")).intValue());
        saveInfo.setProgress(((Number) map.get("progress")).intValue());
        saveInfo.setCreateTime(convertToTime(map.get("createTime")));
        saveInfo.setUpdateTime(convertToTime(map.get("updateTime")));
        return saveInfo;
    }

    public static Map<String, Object> saveInfoToMap(SaveInfo saveInfo) {
        if (saveInfo == null) {
            logger.error("存档对象映射Map异常: 空对象");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", saveInfo.getId());
        map.put("userId", saveInfo.getUserId());
        map.put("playerId", saveInfo.getPlayerId());
        map.put("level", saveInfo.getLevel());
        map.put("exp", saveInfo.getExp());
        map.put("currentHp", saveInfo.getCurrentHp());
        map.put("currentMp", saveInfo.getCurrentMp());
        map.put("floor", saveInfo.getFloor());
        map.put("battleOrder", saveInfo.getBattleOrder());
        map.put("progress", saveInfo.getProgress());
        map.put("createTime", saveInfo.getCreateTime());
        map.put("updateTime", saveInfo.getUpdateTime());
        return map;
    }

    public static BattleInfo mapToBattle(Map<String, Object> map) {
        if (map.isEmpty()) {
            logger.error("Map转换战斗信息对象异常:空Map");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        BattleInfo battleInfo = new BattleInfo();
        battleInfo.setId(((Number) map.get("id")).longValue());
        battleInfo.setSaveId(((Number) map.get("saveId")).longValue());
        battleInfo.setPlayerCurrentHp(((Number) map.get("playerCurrentHp")).intValue());
        battleInfo.setPlayerCurrentMp(((Number) map.get("playerCurrentMp")).intValue());
        battleInfo.setPlayerCurrentDefend(((Number) map.get("playerCurrentDefend")).intValue());
        battleInfo.setMonsterId(((Number) map.get("monsterId")).longValue());
        battleInfo.setMonsterCurrentHp(((Number) map.get("monsterCurrentHp")).intValue());
        battleInfo.setMonsterCurrentMp(((Number) map.get("monsterCurrentMp")).intValue());
        battleInfo.setMonsterCurrentDefend(((Number) map.get("monsterCurrentDefend")).intValue());
        battleInfo.setCreateTime(convertToTime(map.get("createTime")));
        battleInfo.setUpdateTime(convertToTime(map.get("updateTime")));
        return battleInfo;
    }

    public static Map<String, Object> battleToMap(BattleInfo battleInfo) {
        if (battleInfo == null) {
            logger.error("战斗信息对象映射Map异常:空对象");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", battleInfo.getId());
        map.put("saveId", battleInfo.getSaveId());
        map.put("playerCurrentHp", battleInfo.getPlayerCurrentHp());
        map.put("playerCurrentMp", battleInfo.getPlayerCurrentMp());
        map.put("playerCurrentDefend", battleInfo.getPlayerCurrentDefend());
        map.put("monsterId", battleInfo.getMonsterId());
        map.put("monsterCurrentHp", battleInfo.getMonsterCurrentHp());
        map.put("monsterCurrentMp", battleInfo.getMonsterCurrentMp());
        map.put("monsterCurrentDefend", battleInfo.getPlayerCurrentDefend());
        map.put("createTime", battleInfo.getCreateTime());
        map.put("updateTime", battleInfo.getUpdateTime());
        return map;
    }

    public static PlayerActionInfo mapToPa(Map<String, Object> map) {
        if (map.isEmpty()) {
            logger.error("Map转换角色技能对象异常:空Map");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        PlayerActionInfo playerActionInfo = new PlayerActionInfo();
        playerActionInfo.setId(((Number) map.get("id")).longValue());
        playerActionInfo.setBattleId(((Number) map.get("battleId")).longValue());
        playerActionInfo.setPlayerId(((Number) map.get("playerId")).longValue());
        playerActionInfo.setActionId(((Number) map.get("actionId")).longValue());
        playerActionInfo.setCurrentCd(((Number) map.get("currentCd")).intValue());
        playerActionInfo.setRestContinueRound(((Number) map.get("restContinueRound")).intValue());
        playerActionInfo.setCreateTime(convertToTime(map.get("createTime")));
        playerActionInfo.setUpdateTime(convertToTime(map.get("updateTime")));
        return playerActionInfo;
    }

    public static Map<String, Object> paToMap(PlayerActionInfo playerActionInfo) {
        if (playerActionInfo == null) {
            logger.error("角色技能对象映射Map异常:空对象");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", playerActionInfo.getId());
        map.put("battleId", playerActionInfo.getBattleId());
        map.put("playerId", playerActionInfo.getPlayerId());
        map.put("actionId", playerActionInfo.getActionId());
        map.put("currentCd", playerActionInfo.getCurrentCd());
        map.put("restContinueRound", playerActionInfo.getRestContinueRound());
        map.put("createTime", playerActionInfo.getCreateTime());
        map.put("updateTime", playerActionInfo.getUpdateTime());
        return map;
    }

    //todo:提取公共方法
    public static MonsterActionInfo mapToMa(Map<String, Object> map) {
        if (map.isEmpty()) {
            logger.error("Map转换魔物技能对象异常:空Map");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        MonsterActionInfo monsterActionInfo = new MonsterActionInfo();
        monsterActionInfo.setId(((Number) map.get("id")).longValue());
        monsterActionInfo.setBattleId(((Number) map.get("battleId")).longValue());
        monsterActionInfo.setMonsterId(((Number) map.get("monsterId")).longValue());
        monsterActionInfo.setActionId(((Number) map.get("actionId")).longValue());
        monsterActionInfo.setCurrentCd(((Number) map.get("currentCd")).intValue());
        monsterActionInfo.setRestContinueRound(((Number) map.get("restContinueRound")).intValue());
        monsterActionInfo.setCreateTime(convertToTime(map.get("createTime")));
        monsterActionInfo.setUpdateTime(convertToTime(map.get("updateTime")));
        return monsterActionInfo;
    }

    public static Map<String, Object> maToMap(MonsterActionInfo monsterActionInfo) {
        if (monsterActionInfo == null) {
            logger.error("魔物技能对象映射Map异常:空对象");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", monsterActionInfo.getId());
        map.put("battleId", monsterActionInfo.getBattleId());
        map.put("monsterId", monsterActionInfo.getMonsterId());
        map.put("actionId", monsterActionInfo.getActionId());
        map.put("currentCd", monsterActionInfo.getCurrentCd());
        map.put("restContinueRound", monsterActionInfo.getRestContinueRound());
        map.put("createTime", monsterActionInfo.getCreateTime());
        map.put("updateTime", monsterActionInfo.getUpdateTime());
        return map;
    }

}
