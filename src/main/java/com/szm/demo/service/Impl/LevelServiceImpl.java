package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.PlayerClass;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.entity.LevelInfo;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.mapper.LevelInfoMapper;
import com.szm.demo.mapper.UserPlayerInfoMapper;
import com.szm.demo.service.LevelService;
import com.szm.demo.service.UserService;
import com.szm.demo.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class LevelServiceImpl implements LevelService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @Autowired
    LevelInfoMapper levelInfoMapper;

    @Autowired
    UserPlayerInfoMapper userPlayerInfoMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public LevelInfo getLevelInfo(PlayerClass playerClass, Integer level) {
        if (level == null || level <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        String key = RedisKeyConstants.LEVEL_INFO.getKey(playerClass.getValue(), level);
        LevelInfo levelInfo = redisUtil.get(key, LevelInfo.class);
        if (levelInfo == null) {
            levelInfo = levelInfoMapper.getByClassLevel(playerClass, level);
            if (levelInfo == null) {
                logger.error("等级配置缺失");
                throw new BusinessException(ResultCode.NOT_FOUND, "等级配置不存在");
            }
            redisUtil.set(key, levelInfo, 1440, TimeUnit.HOURS);//todo:固定配置的过期时间调整
        }
        logger.info("查询了一次等级信息");
        return levelInfo;
    }

    /**
     * 升级,需配合检测经验
     *
     * @param extraExp 多余的经验
     * @return 新等级的经验
     */
    @Override
    @Transactional
    public Long levelUp(Long extraExp) {
        Long playerId = GameContext.getPlayerId();
        UserPlayerInfo userPlayerInfo = userService.getPlayerInfo();
        int nextLevel = userPlayerInfo.getLevel() + 1;
        if (nextLevel >= 37) {//todo:修改等级上限配置
            userService.setExp(playerId, 0L);
            return -1L;
        }
        try {
            LevelInfo levelInfo = getLevelInfo(userPlayerInfo.getPlayerClass(), nextLevel);

            userPlayerInfo.setLevel(nextLevel);
            userPlayerInfo.setExp(extraExp);
            userPlayerInfo.setUpdateTime(LocalDateTime.now());
            String key = RedisKeyConstants.USER_PLAYER.getKey(playerId);
            userPlayerInfoMapper.updateAllById(userPlayerInfo);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisUtil.set(key, userPlayerInfo, 1440, TimeUnit.MINUTES);//todo:改用HASH
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("角色ID[{}]:升级失败", userPlayerInfo.getId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        logger.info("角色ID[{}]:升级成功,当前等级:{},多余经验值:{}", playerId, nextLevel, extraExp);
        return extraExp;
    }
}
