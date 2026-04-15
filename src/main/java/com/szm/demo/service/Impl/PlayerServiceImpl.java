package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.PlayerClass;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.dto.PlayerShowResp;
import com.szm.demo.entity.LevelInfo;
import com.szm.demo.entity.PlayerActionInfo;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.mapper.LevelInfoMapper;
import com.szm.demo.mapper.PlayerActionInfoMapper;
import com.szm.demo.mapper.UserPlayerInfoMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserPlayerInfoMapper userPlayerInfoMapper;

    @Autowired
    LevelInfoMapper levelInfoMapper;

    @Autowired
    PlayerActionInfoMapper playerActionInfoMapper;

    @Autowired
    LevelService levelService;

    @Autowired
    UserService userService;

    @Override
    @Transactional//抛出异常自动回滚
    public void createPlayer(PlayerClass playerClass) {
        Long userId = GameContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        try {
            LevelInfo levelInfo = levelInfoMapper.getByClassLevel(playerClass, 1);
            UserPlayerInfo userPlayerInfo = new UserPlayerInfo();
            userPlayerInfo.setUserId(userId);
            userPlayerInfo.setPlayerClass(playerClass);
            userPlayerInfo.setLevel(levelInfo.getLevel());
            userPlayerInfo.setExp(0L);
            userPlayerInfo.setAttackBase(levelInfo.getAttackBase());
            userPlayerInfo.setCurrentHp(levelInfo.getMaxHp());
            userPlayerInfo.setCurrentMp(levelInfo.getMaxMp());
            userPlayerInfo.setCreateTime(LocalDateTime.now());
            userPlayerInfo.setUpdateTime(LocalDateTime.now());
            userPlayerInfoMapper.insert(userPlayerInfo);
            Long playerId = userPlayerInfo.getId();
            //创建角色技能组//todo:技能组相关方法解耦
            List<PlayerActionInfo> playerActionInfoList = new ArrayList<>();
            for (long i = 1L; i <= 5; i++) {
                PlayerActionInfo playerActionInfo = new PlayerActionInfo();
                playerActionInfo.setBattleId(0L);
                playerActionInfo.setPlayerId(userId);//todo:
                playerActionInfo.setActionId(i);
                playerActionInfo.setCurrentCd(0);
                playerActionInfo.setRestContinueRound(0);
                playerActionInfo.setCreateTime(LocalDateTime.now());
                playerActionInfo.setUpdateTime(LocalDateTime.now());
                playerActionInfoList.add(playerActionInfo);
            }
            playerActionInfoMapper.batchInsert(playerActionInfoList);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.USER_PLAYER.getKey(playerId);//todo:调整为HASH
                            redisUtil.set(key, userPlayerInfo, 1440, TimeUnit.MINUTES);
                        }
                    }
            );
            logger.info("用户ID[{}]:创建角色成功", userId);
        } catch (Exception e) {
            logger.error("用户ID[{}]:创建角色失败", userId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override//todo:待优化
    public PlayerShowResp showPlayer() {
        Long userId = GameContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }//todo:4.15
        String key = RedisKeyConstants.PLAYER_SHOW.getKey(userId);
        PlayerShowResp resp = redisUtil.get(key, PlayerShowResp.class);
        if (resp == null) {
            resp = new PlayerShowResp();
            UserPlayerInfo userPlayerInfo = userService.getPlayerInfo(userId);
            LevelInfo levelInfo = levelService.getLevelInfo(userPlayerInfo.getLevel());
            resp.setPlayerClass(userPlayerInfo.getPlayerClass());
            resp.setLevel(userPlayerInfo.getLevel());
            resp.setMaxHp(levelInfo.getMaxHp());
            resp.setMaxMp(levelInfo.getMaxMp());
            resp.setAttackBase(levelInfo.getAttackBase());
            resp.setExp(userPlayerInfo.getExp());
            redisUtil.set(key, resp, 30, TimeUnit.MINUTES);
        }
        return resp;
    }

    @Override
    public void resetPlayer(Long userId) {
        LevelInfo levelInfo = levelService.getLevelInfo(1);
        UserPlayerInfo userPlayerInfo = userService.getPlayerInfo(userId);
        userPlayerInfo.setExp(0L);
        userPlayerInfo.setLevel(levelInfo.getLevel());
        userPlayerInfo.setUpdateTime(LocalDateTime.now());
        userService.setPlayerInfo(userPlayerInfo);
        String key = RedisKeyConstants.USER_PLAYER.getKey(userId);//todo:修订redis
        redisUtil.delete(key);
        logger.warn("用户ID[{}]:重置了角色", userId);
    }

    /**
     * 检测经验溢出情况
     *
     * @param playerId 角色ID
     * @return 当前经验-所需经验(大于零为溢出的经验,小于零为欠缺的经验)
     */
    @Override
    public Long checkOverflowExp(Long playerId) {
        UserPlayerInfo userPlayerInfo = userService.getPlayerInfo(playerId);
        long currentExp = userPlayerInfo.getExp();
        int currentLevel = userPlayerInfo.getLevel();
        LevelInfo levelInfo = levelService.getLevelInfo(currentLevel);
        logger.info("用户ID[{}]:检测当前经验:{}, 升级所需经验:{}", playerId, currentExp, levelInfo.getNeededExp());
        return currentExp - levelInfo.getNeededExp();
    }


    @Override
    // 排坑: 这里不能加事务,否则因为事务传播, 同一个事务内反复读，永远读到的是旧数据
    public void tryLevelUp(Long playerId) {
        long startTime = System.currentTimeMillis();
        logger.info("角色ID[{}]:尝试进行升级:开始", playerId);
        if (playerId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        Long extraExp = checkOverflowExp(playerId);
        if (extraExp < 0) {
            return;
        }
        while (extraExp >= 0) {
            levelService.levelUp(playerId, extraExp);
            extraExp = checkOverflowExp(playerId);
            logger.info("角色ID[{}]:尝试进行升级:----当前多余经验:{}", playerId, extraExp);
        }
        logger.info("角色ID[{}]:尝试进行升级:结束，总耗时={}ms", playerId, System.currentTimeMillis() - startTime);
    }
}
