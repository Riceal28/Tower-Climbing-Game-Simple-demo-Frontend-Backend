package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.mapper.SaveInfoMapper;
import com.szm.demo.service.SaveProviderService;
import com.szm.demo.util.MapUtil;
import com.szm.demo.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;
import java.util.Objects;

@Service
public class SaveProviderServiceImpl implements SaveProviderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    SaveInfoMapper saveInfoMapper;
    @Autowired
    RedisUtil redisUtil;
    /**
     * 更新存档
     * SaveInfo -> [UPDATE] ->
     * {SAVE_LIST -> setAdd, SAVE_DETAIL -> hashPutAll}
     *
     * @param saveInfo 修改后的存档
     */
    @Override
    @Transactional//todo:设置过期时间
    public void updateSave(SaveInfo saveInfo) {
        Long userId = GameContext.getUserId();
        Long playerId = GameContext.getPlayerId();
        if (playerId == null || userId == null) {//todo:优化判断逻辑
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        if (saveInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        if (!Objects.equals(saveInfo.getPlayerId(), playerId)) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
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
                                    SAVE_DETAIL.getKey(userId, saveInfo.getId());
                            Map<String, Object> map = MapUtil.saveInfoToMap(saveInfo);
                            redisUtil.hashPutAll(key2, map);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("用户ID[{}]:更新存档失败", saveInfo.getUserId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
