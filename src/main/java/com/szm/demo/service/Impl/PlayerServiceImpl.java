package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.entity.LevelInfo;
import com.szm.demo.entity.UserDetail;
import com.szm.demo.mapper.LevelInfoMapper;
import com.szm.demo.mapper.UserDetailMapper;
import com.szm.demo.service.LevelService;
import com.szm.demo.service.PlayerService;
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
public class PlayerServiceImpl implements PlayerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserDetailMapper userDetailMapper;

    @Autowired
    LevelInfoMapper levelInfoMapper;

    @Autowired
    LevelService levelService;

    @Autowired
    UserService userService;

    @Override
    @Transactional//抛出异常自动回滚
    public void createDefaultPlayer(Long userId) {
        if (userDetailMapper.getByUserId(userId) != null) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "已拥有角色");
        }
        try {
            LevelInfo levelInfo = levelInfoMapper.getByLevel(1);
            UserDetail userDetail = new UserDetail();
            userDetail.setUserId(userId);
            userDetail.setLevel(levelInfo.getLevel());
            userDetail.setExp(0L);
            userDetail.setCurrentHp(levelInfo.getMaxHp());
            userDetail.setCurrentMp(levelInfo.getMaxMp());
            userDetail.setAttackBase(levelInfo.getAttackBase());
            userDetail.setCreateTime(LocalDateTime.now());
            userDetail.setUpdateTime(LocalDateTime.now());
            userDetailMapper.insert(userDetail);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.USER_DETAIL.getKey(userId);
                            redisUtil.set(key, userDetail, 1440, TimeUnit.MINUTES);
                        }
                    }
            );
            logger.info("用户ID[{}]:创建角色成功", userId);
        } catch (Exception e) {
            logger.error("用户ID[{}]:创建角色失败", userId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public void resetPlayer(Long userId) {
        LevelInfo levelInfo = levelService.getLevelInfo(1);
        UserDetail userDetail = userService.getUserDetail(userId);
        userDetail.setExp(0L);
        userDetail.setLevel(levelInfo.getLevel());
        userDetail.setAttackBase(levelInfo.getAttackBase());
        userDetail.setCurrentHp(levelInfo.getMaxHp());
        userDetail.setCurrentMp(levelInfo.getMaxHp());
        userDetail.setUpdateTime(LocalDateTime.now());
        userService.setUserDetail(userDetail);
        String key = RedisKeyConstants.USER_DETAIL.getKey(userId);
        redisUtil.delete(key);
        logger.warn("用户ID[{}]:重置了角色",userId);
    }

    /**
     * 检测经验溢出情况
     *
     * @param userId 用户ID
     * @return 当前经验-所需经验(大于零为溢出的经验,小于零为欠缺的经验)
     */
    @Override
    public Long checkOverflowExp(Long userId) {
        UserDetail userDetail = userService.getUserDetail(userId);
        long currentExp = userDetail.getExp();
        int currentLevel = userDetail.getLevel();
        LevelInfo levelInfo = levelService.getLevelInfo(currentLevel);
        logger.info("用户ID[{}]:检测当前经验:{}, 升级所需经验:{}", userId, currentExp, levelInfo.getNeededExp());
        return currentExp - levelInfo.getNeededExp();
    }


    @Override
    // 排坑: 这里不能加事务,否则因为事务传播, 同一个事务内反复读，永远读到的是旧数据
    public void tryLevelUp(Long userId) {
        long startTime = System.currentTimeMillis();
        logger.info("用户ID[{}]:尝试进行升级:开始", userId);
        if (userId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        Long extraExp = checkOverflowExp(userId);
        if (extraExp < 0) {
            return;
        }
        while (extraExp >= 0) {
            levelService.levelUp(userId, extraExp);
            extraExp = checkOverflowExp(userId);
            logger.info("用户ID[{}]:尝试进行升级:----当前多余经验:{}", userId, extraExp);
        }
        logger.info("用户ID[{}]:尝试进行升级:结束，总耗时={}ms", userId, System.currentTimeMillis() - startTime);
    }
}
