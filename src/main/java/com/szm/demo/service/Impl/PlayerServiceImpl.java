package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.PlayerClass;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.dto.PlayerShowResp;
import com.szm.demo.entity.ActionInfo;
import com.szm.demo.entity.LevelInfo;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.mapper.UserPlayerInfoMapper;
import com.szm.demo.service.ActionService;
import com.szm.demo.service.LevelService;
import com.szm.demo.service.PlayerProviderService;
import com.szm.demo.service.PlayerService;
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
import java.util.concurrent.TimeUnit;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserPlayerInfoMapper userPlayerInfoMapper;

    @Autowired
    PlayerProviderService playerProviderService;

    @Autowired
    LevelService levelService;

    @Autowired
    ActionService actionService;

    @Override
    @Transactional//抛出异常自动回滚
    public void createPlayer(PlayerClass playerClass) {
        Long userId = GameContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        try {
            LevelInfo levelInfo = levelService.getLevelInfo(playerClass, 1);
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

            actionService.addDefaultAction();

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.USER_PLAYER.getKey(playerId);
                            Map<String, Object> map = MapUtil.playerToMap(userPlayerInfo);
                            redisUtil.hashPutAll(key, map);
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
    @Transactional
    public void updatePlayerBySave(SaveInfo saveInfo) {
        if (saveInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        UserPlayerInfo userPlayerInfo = saveToPlayer(saveInfo);
        playerProviderService.updatePlayerInfo(userPlayerInfo);
    }

    private UserPlayerInfo saveToPlayer(SaveInfo saveInfo) {
        if (saveInfo == null) {
            logger.error("存档读取异常: 空对象");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        UserPlayerInfo userPlayerInfo = playerProviderService.getPlayerInfo();
        LevelInfo levelInfo =
                levelService.getLevelInfo(userPlayerInfo.getPlayerClass(), saveInfo.getLevel());
        userPlayerInfo.setLevel(saveInfo.getLevel());
        userPlayerInfo.setExp(saveInfo.getExp());
        userPlayerInfo.setAttackBase(levelInfo.getAttackBase());
        userPlayerInfo.setCurrentHp(saveInfo.getCurrentHp());
        userPlayerInfo.setCurrentMp(saveInfo.getCurrentMp());
        userPlayerInfo.setUpdateTime(LocalDateTime.now());
        return userPlayerInfo;
    }

    @Override//todo:待优化
    public PlayerShowResp showPlayer() {
        Long playerId = GameContext.getPlayerId();
        if (playerId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        String key = RedisKeyConstants.PLAYER_SHOW.getKey(playerId);
        PlayerShowResp resp = redisUtil.get(key, PlayerShowResp.class);//todo:HASH 低优先级
        if (resp == null) {
            resp = new PlayerShowResp();
            UserPlayerInfo userPlayerInfo = playerProviderService.getPlayerInfo();
            LevelInfo levelInfo = levelService.getLevelInfo(userPlayerInfo.getPlayerClass(), userPlayerInfo.getLevel());
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
    @Transactional
    public void resetPlayer(Long playerId) {
        Long userId = GameContext.getUserId();
        UserPlayerInfo userPlayerInfo = playerProviderService.getPlayerInfo();
        if (!Objects.equals(userPlayerInfo.getUserId(), userId)) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "非法操作");
        }
        LevelInfo levelInfo = levelService.getLevelInfo(userPlayerInfo.getPlayerClass(), 1);
        userPlayerInfo.setExp(0L);
        userPlayerInfo.setLevel(levelInfo.getLevel());
        userPlayerInfo.setAttackBase(levelInfo.getAttackBase());
        userPlayerInfo.setCurrentHp(levelInfo.getMaxHp());
        userPlayerInfo.setCurrentMp(levelInfo.getMaxMp());
        userPlayerInfo.setUpdateTime(LocalDateTime.now());
        playerProviderService.updatePlayerInfo(userPlayerInfo);
        logger.warn("角色ID[{}]:角色被重置了", playerId);
    }

    /**
     * 检测经验溢出情况
     *
     * @return 当前经验-所需经验(大于零为溢出的经验,小于零为欠缺的经验)
     */
    @Override
    public Long checkOverflowExp() {
        Long playerId = GameContext.getPlayerId();
        if (playerId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        UserPlayerInfo userPlayerInfo = playerProviderService.getPlayerInfo();
        long currentExp = userPlayerInfo.getExp();
        int currentLevel = userPlayerInfo.getLevel();
        LevelInfo levelInfo = levelService.getLevelInfo(userPlayerInfo.getPlayerClass(), currentLevel);
        logger.info("角色ID[{}]:检测当前经验:{}, 升级所需经验:{}", playerId, currentExp, levelInfo.getNeededExp());
        return currentExp - levelInfo.getNeededExp();
    }


    @Override
    // 排坑: 这里不能加事务,否则因为事务传播, 循环升级处于同一个事务内,数据不会更新,永远读到的是旧数据
    public void tryLevelUp() {
        Long playerId = GameContext.getPlayerId();
        long startTime = System.currentTimeMillis();
        logger.info("角色ID[{}]:尝试进行升级:开始", playerId);
        if (playerId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        Long extraExp = checkOverflowExp();
        if (extraExp < 0) {
            return;
        }
        while (extraExp >= 0) {
            levelService.levelUp(extraExp);
            extraExp = checkOverflowExp();
            logger.info("角色ID[{}]:尝试进行升级:当前多余经验:{}", playerId, extraExp);
        }
        logger.info("角色ID[{}]:尝试进行升级:结束，总耗时={}ms", playerId, System.currentTimeMillis() - startTime);
    }

    @Override
    @Transactional
    public void afterActionByPlayer(UserPlayerInfo userPlayerInfo, ActionInfo actionInfo) {
        if (userPlayerInfo == null || actionInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        if (userPlayerInfo.getCurrentMp() < actionInfo.getMpCost()) {
            throw new BusinessException(ResultCode.MP_NOT_ENOUGH);
        }
        int forHp = actionInfo.getForHp();
        int forMp = actionInfo.getForMp();
        int forDefend = actionInfo.getForDefend();//todo: 更新格挡值
        int mpCost = actionInfo.getMpCost();
        LevelInfo levelInfo =
                levelService.getLevelInfo(userPlayerInfo.getPlayerClass(), userPlayerInfo.getLevel());
        if (actionInfo.getIsTargetPlayer()) {
            userPlayerInfo.setCurrentHp(Math.max(0, userPlayerInfo.getCurrentHp() + forHp));
            userPlayerInfo.setCurrentMp(Math.max(0, userPlayerInfo.getCurrentMp() + forMp - mpCost));
            //限制超出值 todo:可能的优化
            userPlayerInfo.setCurrentHp(Math.min(levelInfo.getMaxHp(), userPlayerInfo.getCurrentHp()));
            userPlayerInfo.setCurrentMp(Math.min(levelInfo.getMaxMp(), userPlayerInfo.getCurrentMp()));
        } else {

        }
        playerProviderService.updatePlayerInfo(userPlayerInfo);
    }
}
