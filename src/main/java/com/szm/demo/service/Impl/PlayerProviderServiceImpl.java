package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.mapper.UserPlayerInfoMapper;
import com.szm.demo.service.PlayerProviderService;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PlayerProviderServiceImpl implements PlayerProviderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserPlayerInfoMapper userPlayerInfoMapper;

    @Override//todo:try包围
    public UserPlayerInfo getPlayerInfo() {
        Long playerId = GameContext.getPlayerId();
        if (playerId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        String key = RedisKeyConstants.USER_PLAYER.getKey(playerId);
        Map<String, Object> map = redisUtil.hashEntries(key, Object.class);
        if (!CollectionUtils.isEmpty(map)) {
            return MapUtil.mapToPlayer(map);
        }
        UserPlayerInfo userPlayerInfo = userPlayerInfoMapper.getById(playerId);
        if (userPlayerInfo == null) {
            logger.error("用户角色信息不存在");
            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
        }
        Map<String, Object> map2 = MapUtil.playerToMap(userPlayerInfo);
        redisUtil.hashPutAll(key, map2);

        logger.info("查询了一次用户角色详情");
        return userPlayerInfo;
    }

    @Override
    public List<UserPlayerInfo> getPlayerInfoByUserId() {
        Long userId = GameContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        logger.info("userId[{}]",userId);
        List<UserPlayerInfo> userPlayerInfos = userPlayerInfoMapper.getAllByUserId(userId);
//        if (userPlayerInfos.isEmpty()) {
//            logger.error("用户无角色");
//            throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
//        }
        logger.info("角色列表[{}]",userPlayerInfos);
        logger.info("批量查询了一次用户角色详情");
        return userPlayerInfos;
    }

    @Override
    @Transactional
    public void updatePlayerInfo(UserPlayerInfo userPlayerInfo) {
        if (userPlayerInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        if (userPlayerInfo.getId() == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        Long playerId = GameContext.getPlayerId();
        if (playerId == null || !Objects.equals(userPlayerInfo.getId(), playerId)) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }

        String key = RedisKeyConstants.USER_PLAYER.getKey(playerId);
        try {
            userPlayerInfoMapper.updateAllById(userPlayerInfo);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            Map<String, Object> map = MapUtil.playerToMap(userPlayerInfo);
                            redisUtil.hashPutAll(key, map);
                        }
                    }
            );
            logger.info("角色ID[{}]:更新信息成功,{}", playerId, userPlayerInfo);
        } catch (Exception e) {
            logger.error("角色ID[{}]:更新信息失败", playerId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
