package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserDetail;
import com.szm.demo.mapper.SaveInfoMapper;
import com.szm.demo.mapper.UserDetailMapper;
import com.szm.demo.service.SaveService;
import com.szm.demo.service.UserService;
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
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SaveServiceImpl implements SaveService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserService userService;

    @Autowired
    SaveInfoMapper saveInfoMapper;

    /**
     * 创建默认存档
     * UserDetail -> SaveInfo -> [INSERT]
     * {SAVE_DETAIL -> hashPutAll, SAVE_LIST -> setAdd}
     *
     * @param userId 用户ID
     */
    @Override
    @Transactional
    public void createDefaultSave(Long userId) {
        try {
            SaveInfo saveInfo = new SaveInfo();
            UserDetail userDetail = userService.getUserDetail(userId);
            saveInfo.setUserId(userId);
            saveInfo.setLevel(userDetail.getLevel());
            saveInfo.setExp(userDetail.getExp());
            saveInfo.setFloor(1);
            saveInfo.setMonsterId(0L);
            saveInfo.setIsActive(false);
            saveInfo.setProgress(0);
            saveInfo.setCreateTime(LocalDateTime.now());
            saveInfo.setUpdateTime(LocalDateTime.now());
            saveInfoMapper.insert(saveInfo);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.SAVE_DETAIL.getKey(userId, saveInfo.getId());
                            Map<String, Object> map = saveInfoToMap(saveInfo);
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
     * @param userId 用户ID
     * @return List-SaveInfo
     */
    @Override//todo:设置过期时间
    public List<SaveInfo> getSaveByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
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
                SaveInfo saveInfo = mapToSaveInfo(map);
                if (saveInfo != null) {
                    saveInfoList.add(saveInfo);
                }
            }
        }
        if (!saveInfoList.isEmpty()) {
            return saveInfoList;
        }
        saveInfoList = saveInfoMapper.getByUserId(userId);

        for (SaveInfo saveInfo : saveInfoList) {
            Long id = saveInfo.getId();
            String key3 = RedisKeyConstants.SAVE_LIST.getKey(userId);
            redisUtil.setAdd(key3, id);
            String detailKey = RedisKeyConstants.SAVE_DETAIL.getKey(userId, id);
            Map<String, Object> map = saveInfoToMap(saveInfo);
            if (!redisUtil.hasKey(detailKey)) {
                redisUtil.hashPutAll(detailKey, map);
            }
        }
        return saveInfoList;
    }

    /**
     * 根据用户ID,存档ID获取指定存档
     * {SAVE_DETAIL -> hashEntries} ->
     * IF EMPTY -> [SELECT] -> SaveInfo ->
     * {SAVE_DETAIL -> hashPutAll, SAVE_LIST -> setAdd}
     *
     * @param userId 用户ID
     * @param id     存档ID
     * @return SaveInfo
     */
    @Override//todo:设置过期时间
    public SaveInfo getSaveById(Long userId, Long id) {
        if (id == null || userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        String key = RedisKeyConstants.SAVE_DETAIL.getKey(userId, id);
        Map<String, Object> map = redisUtil.hashEntries(key, Object.class);
        if (!CollectionUtils.isEmpty(map)) {
            SaveInfo saveInfo = mapToSaveInfo(map);
            if (saveInfo != null) {
                return saveInfo;
            }
        }
        SaveInfo saveInfo = saveInfoMapper.getById(id);
        if (saveInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "未找到存档信息");
        }
        Map<String, Object> map2 = saveInfoToMap(saveInfo);
        redisUtil.hashPutAll(key, map2);
        String key3 = RedisKeyConstants.SAVE_LIST.getKey(userId);
        redisUtil.setAdd(key3, id);
        return saveInfo;
    }

    /**
     * 更新存档
     * SaveInfo -> [UPDATE] ->
     * {SAVE_LIST -> setAdd, SAVE_DETAIL -> hashPutAll}
     *
     * @param saveInfo 修改后的存档
     */
    @Override
    @Transactional//todo:设置过期时间
    public void updateSave(SaveInfo saveInfo, Long userId) {
        if (saveInfo == null || userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            saveInfoMapper.updateSaveById(saveInfo);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.SAVE_LIST.getKey(userId);
                            redisUtil.setAdd(key, saveInfo.getId());
                            String key2 = RedisKeyConstants.
                                    SAVE_DETAIL.getKey(saveInfo.getUserId(), saveInfo.getId());
                            Map<String, Object> map = saveInfoToMap(saveInfo);
                            redisUtil.hashPutAll(key2, map);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("用户ID[{}]:更新存档失败", saveInfo.getUserId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional//todo:设置过期时间
    public void clearAllActiveSave(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            saveInfoMapper.clearActiveByUserId(userId);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.SAVE_LIST.getKey(userId);
                            Set<String> saveIdSet = redisUtil.setMembers(key, String.class);
                            // 遍历清除激活状态
                            for (String sid : saveIdSet) {
                                String key2 = RedisKeyConstants.SAVE_DETAIL.getKey(userId, sid);
                                redisUtil.hashPut(key2, "isActive", false);
                            }
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("用户ID[{}]:关闭所有存档失败", userId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 根据用户ID,存档ID激活指定存档
     * [UPDATE ALL] -> SaveInfo.isActive.flase ->
     * getSaveById -> SaveInfo.isActive.true -> [UPDATE] ->
     * {SAVE_LIST -> setAdd setMembers, SAVE_DETAIL -> hashPut[setM](false) hashPut(true)}
     *
     * @param userId 用户ID
     * @param id     存档ID
     */
    @Override
    @Transactional//todo:设置过期时间
    public void setSaveActive(Long userId, Long id) {
        if (userId == null || id == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        SaveInfo saveInfo = getSaveById(userId, id);
        try {
            saveInfoMapper.clearActiveByUserId(userId);
            saveInfo.setIsActive(true);
            updateSave(saveInfo, userId);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            // 先更新存档ID集
                            String key = RedisKeyConstants.SAVE_LIST.getKey(userId);
                            redisUtil.setAdd(key, id);
                            Set<String> saveIdSet = redisUtil.setMembers(key, String.class);
                            // 遍历清除激活状态
                            for (String sid : saveIdSet) {
                                String key2 = RedisKeyConstants.SAVE_DETAIL.getKey(userId, sid);
                                redisUtil.hashPut(key2, "isActive", false);
                            }
                            // 单独激活所选存档
                            String key3 = RedisKeyConstants.SAVE_DETAIL.getKey(userId, id);
                            redisUtil.hashPut(key3, "isActive", true);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("用户ID[{}]:激活存档ID[{}]失败", userId, id, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 根据用户ID查询激活的存档
     *
     * @param userId 用户ID
     * @return SaveInfo
     */
    @Override//todo:设置过期时间
    public SaveInfo getActiveSave(Long userId) {
        if (userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            String key = RedisKeyConstants.SAVE_LIST.getKey(userId);
            Set<String> saveIdSet = redisUtil.setMembers(key, String.class);
            if (!saveIdSet.isEmpty()) {
                for (String sid : saveIdSet) {
                    String detailKey = RedisKeyConstants.SAVE_DETAIL.getKey(userId, sid);
                    Boolean result = redisUtil.hashGet(detailKey, "isActive", Boolean.class);
                    if (result == false) {
                        continue;
                    }
                    Map<String, Object> map = redisUtil.hashEntries(detailKey, Object.class);
                    return mapToSaveInfo(map);
                }
            }
            SaveInfo saveInfo = saveInfoMapper.getActiveSave(userId);
            if (saveInfo == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "未找到已激活的存档");
            }
            String key2 = RedisKeyConstants.SAVE_LIST.getKey(userId);
            redisUtil.setAdd(key2, saveInfo.getId());
            String key3 = RedisKeyConstants.SAVE_DETAIL.getKey(userId, saveInfo.getId());
            if (!redisUtil.hasKey(key3)) {
                redisUtil.hashPutAll(key3, saveInfoToMap(saveInfo));
            }
            return saveInfo;
        } catch (Exception e) {
            logger.error("用户ID[{}]:获取已激活存档失败", userId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }

    }

    private SaveInfo mapToSaveInfo(Map<String, Object> map) {
        SaveInfo saveInfo = new SaveInfo();
        saveInfo.setId(((Number) map.get("id")).longValue());
        saveInfo.setUserId(((Number) map.get("userId")).longValue());
        saveInfo.setLevel(((Number) map.get("level")).intValue());
        saveInfo.setExp(((Number) map.get("exp")).longValue());
        saveInfo.setFloor(((Number) map.get("floor")).intValue());
        saveInfo.setProgress(((Number) map.get("progress")).intValue());
        saveInfo.setMonsterId(((Number) map.get("monsterId")).longValue());
        saveInfo.setIsActive((Boolean) map.get("isActive"));
        saveInfo.setCreateTime((LocalDateTime) map.get("createTime"));
        saveInfo.setUpdateTime((LocalDateTime) map.get("updateTime"));
        return saveInfo;
    }

    private Map<String, Object> saveInfoToMap(SaveInfo saveInfo) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("id", saveInfo.getId());
        map.put("userId", saveInfo.getUserId());
        map.put("level", saveInfo.getLevel());
        map.put("exp", saveInfo.getExp());
        map.put("floor", saveInfo.getFloor());
        map.put("progress", saveInfo.getProgress());
        map.put("monsterId", saveInfo.getMonsterId());
        map.put("isActive", saveInfo.getIsActive());
        map.put("createTime", saveInfo.getCreateTime());
        map.put("updateTime", saveInfo.getUpdateTime());
        return map;
    }
}
