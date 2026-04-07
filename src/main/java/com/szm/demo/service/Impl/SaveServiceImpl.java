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
    UserDetailMapper userDetailMapper;

    @Autowired
    SaveInfoMapper saveInfoMapper;

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
            redisUtil.hashPutAll(detailKey, map);
        }
        return saveInfoList;
    }

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
            throw new BusinessException(ResultCode.NOT_FOUND,"未找到存档信息");
        }
        Map<String, Object> map2 = saveInfoToMap(saveInfo);
        redisUtil.hashPutAll(key, map2);
        return saveInfo;
    }

    @Override
    @Transactional//todo:设置过期时间
    public void updateSave(SaveInfo saveInfo) {
        if (saveInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            saveInfoMapper.updateSaveById(saveInfo);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.
                                    SAVE_DETAIL.getKey(saveInfo.getUserId(), saveInfo.getId());
                            Map<String, Object> map = saveInfoToMap(saveInfo);
                            redisUtil.hashPutAll(key,map);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("用户ID[{}]:更新存档失败", saveInfo.getUserId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
    @Override
    @Transactional
    public void setSaveActive(Long userId, Long id) {
        SaveInfo saveInfo = getSaveById(userId,id);
        //todo:调用mapper方法,清除所有激活的save
        saveInfo.setIsActive(true);
        updateSave(saveInfo);
    }

    @Override
    public void getActiveSave(Long userId) {

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
