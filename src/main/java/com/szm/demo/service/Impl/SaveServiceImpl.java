package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.entity.BattleInfo;
import com.szm.demo.entity.LevelInfo;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.mapper.SaveInfoMapper;
import com.szm.demo.service.*;
import com.szm.demo.util.MapUtil;
import com.szm.demo.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SaveServiceImpl implements SaveService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    PlayerProviderService playerProviderService;

    @Autowired
    SaveInfoMapper saveInfoMapper;
    @Autowired
    private LevelService levelService;
    @Autowired
    private SaveProviderService saveProviderService;

    /**
     * 创建默认存档
     * UserDetail -> SaveInfo -> [INSERT]
     * {SAVE_DETAIL -> hashPutAll, SAVE_LIST -> setAdd}
     *
     */
    @Override
    @Transactional
    public void createDefaultSave() {
        Long userId = GameContext.getUserId();
        Long playerId = GameContext.getPlayerId();
        if (userId == null || playerId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        try {
            SaveInfo saveInfo = new SaveInfo();
            UserPlayerInfo userPlayerInfo = playerProviderService.getPlayerInfo();
            LevelInfo levelInfo = levelService
                    .getLevelInfo(userPlayerInfo.getPlayerClass(), userPlayerInfo.getLevel());
            saveInfo.setUserId(userId);//创建初始角色-使用角色创建存档-存档-角色信息界面
            saveInfo.setPlayerId(playerId);
            saveInfo.setLevel(userPlayerInfo.getLevel());
            saveInfo.setExp(userPlayerInfo.getExp());
            saveInfo.setCurrentHp(levelInfo.getMaxHp());
            saveInfo.setCurrentMp(levelInfo.getMaxMp());
            saveInfo.setFloor(1);
            saveInfo.setBattleOrder(0);
            saveInfo.setProgress(0);
            saveInfo.setCreateTime(LocalDateTime.now());
            saveInfo.setUpdateTime(LocalDateTime.now());
            saveInfoMapper.insert(saveInfo);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.SAVE_DETAIL.getKey(userId, saveInfo.getId());
                            Map<String, Object> map = MapUtil.saveInfoToMap(saveInfo);
                            redisUtil.hashPutAll(key, map);//todo:设置过期时间
                            // 保存用户的所有存档ID
                            String key2 = RedisKeyConstants.SAVE_LIST.getKey(userId);
                            redisUtil.setAdd(key2, saveInfo.getId());
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("用户ID[{}]:创建存档失败", userId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 根据用户ID获取SaveInfo列表
     * {SAVE_LIST -> setMembers, SAVE_DETAIL -> hashEntries} ->
     * IF Empty -> [SELECT] -> List-SaveInfo ->
     * {SAVE_LIST -> setAdd, SAVE_DETAIL -> hashPutAll}
     *
     * @return List-SaveInfo
     */
    @Override//todo:设置过期时间
    //todo:try包围
    public List<SaveInfo> getSaveByUserId() {
        Long userId = GameContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        String listKey = RedisKeyConstants.SAVE_LIST.getKey(userId);
        Set<String> saveIdSet = redisUtil.setMembers(listKey, String.class);
        List<SaveInfo> saveInfoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(saveIdSet)) {
            for (String sid : saveIdSet) {
                Long id = Long.parseLong(sid);
                String detailKey = RedisKeyConstants.SAVE_DETAIL.getKey(userId, id);
                Map<String, Object> map = redisUtil.hashEntries(detailKey, Object.class);
                if (CollectionUtils.isEmpty(map)) {
                    continue;
                }
                SaveInfo saveInfo = MapUtil.mapToSaveInfo(map);
                if (saveInfo != null) {
                    saveInfoList.add(saveInfo);
                }
            }
        }
        if (!saveInfoList.isEmpty()) {
            return saveInfoList;
        }
        saveInfoList = saveInfoMapper.getAllByUserId(userId);
        if (saveInfoList.isEmpty()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "当前无可用存档");
        }

        for (SaveInfo saveInfo : saveInfoList) {
            Long id = saveInfo.getId();
            String key3 = RedisKeyConstants.SAVE_LIST.getKey(userId);
            redisUtil.setAdd(key3, id);
            String detailKey = RedisKeyConstants.SAVE_DETAIL.getKey(userId, id);
            Map<String, Object> map = MapUtil.saveInfoToMap(saveInfo);
            if (!redisUtil.hasKey(detailKey)) {
                redisUtil.hashPutAll(detailKey, map);
            }
        }
        return saveInfoList;
    }

    /**
     * 根据用户ID获取SaveInfo列表
     * {SAVE_LIST -> setMembers, SAVE_DETAIL -> hashEntries} ->
     * IF Empty -> [SELECT] -> List-SaveInfo ->
     * {SAVE_LIST -> setAdd, SAVE_DETAIL -> hashPutAll}
     *
     * @return List-SaveInfo
     */
    @Override
    //todo:try包围
    public List<SaveInfo> getSaveByPlayerId() {
        Long playerId = GameContext.getPlayerId();
        if (playerId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        List<SaveInfo> saveInfoList = saveInfoMapper.getByPlayerId(playerId);
        logger.info("当前角色ID[{}]角色存档列表[{}]",playerId,saveInfoList);
        return saveInfoList;
    }
    //todo:try包围
    @Override
    public List<SaveInfo> getSaveByPlayerId(Long playerId) {
        if (playerId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        List<SaveInfo> saveInfoList = saveInfoMapper.getByPlayerId(playerId);
        logger.info("当前角色ID[{}]角色存档列表[{}]",playerId,saveInfoList);
        return saveInfoList;
    }

    /**
     * 根据用户ID,存档ID获取指定存档
     * {SAVE_DETAIL -> hashEntries} ->
     * IF EMPTY -> [SELECT] -> SaveInfo ->
     * {SAVE_DETAIL -> hashPutAll, SAVE_LIST -> setAdd}
     *
     * @return SaveInfo
     */
    @Override//todo:设置过期时间
    public SaveInfo getSaveById() {
        Long userId = GameContext.getUserId();
        Long saveId = GameContext.getSaveId();
        if (saveId == null || userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        String key = RedisKeyConstants.SAVE_DETAIL.getKey(userId, saveId);
        Map<String, Object> map = redisUtil.hashEntries(key, Object.class);
        if (!CollectionUtils.isEmpty(map)) {
            SaveInfo saveInfo = MapUtil.mapToSaveInfo(map);
            if (saveInfo != null) {
                return saveInfo;
            }
        }
        SaveInfo saveInfo = saveInfoMapper.getById(saveId);
        if (saveInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "未找到存档信息");
        }
        Map<String, Object> map2 = MapUtil.saveInfoToMap(saveInfo);
        redisUtil.hashPutAll(key, map2);
        String key3 = RedisKeyConstants.SAVE_LIST.getKey(userId);
        redisUtil.setAdd(key3, saveId);
        return saveInfo;
    }

    /**
     * 保存当前战斗胜利后的存档
     */
    @Override
    public void saveAfterWin(BattleInfo battleInfo) {
        UserPlayerInfo userPlayerInfo = playerProviderService.getPlayerInfo();
        SaveInfo saveInfo = getSaveById();
        saveInfo.setLevel(userPlayerInfo.getLevel());
        saveInfo.setExp(userPlayerInfo.getExp());
        saveInfo.setCurrentHp(userPlayerInfo.getCurrentHp());
        saveInfo.setCurrentMp(userPlayerInfo.getCurrentMp());
        saveInfo.setBattleOrder(saveInfo.getBattleOrder() + 1);
        saveInfo.setUpdateTime(LocalDateTime.now());
        logger.info("saveInfo{}",saveInfo);
        saveProviderService.updateSave(saveInfo);
    }
}
