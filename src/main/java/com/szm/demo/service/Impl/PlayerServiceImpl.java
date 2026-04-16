package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.PlayerClass;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.dto.PlayerShowResp;
import com.szm.demo.entity.LevelInfo;
import com.szm.demo.entity.PlayerActionInfo;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.mapper.LevelInfoMapper;
import com.szm.demo.mapper.PlayerActionInfoMapper;
import com.szm.demo.mapper.UserPlayerInfoMapper;
import com.szm.demo.service.LevelService;
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
    LevelInfoMapper levelInfoMapper;

    @Autowired
    PlayerActionInfoMapper playerActionInfoMapper;

    @Autowired
    LevelService levelService;

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
                playerActionInfo.setPlayerId(playerId);
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

    @Override//todo:try包围
    public UserPlayerInfo getPlayerInfo() {
        Long playerId = GameContext.getPlayerId();
        if (playerId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        String key = RedisKeyConstants.USER_PLAYER.getKey(playerId);
        UserPlayerInfo userPlayerInfo = MapUtil.mapToPlayer(redisUtil.hashEntries(key, Object.class));
        if (userPlayerInfo == null) {
            userPlayerInfo = userPlayerInfoMapper.getById(playerId);
            if (userPlayerInfo == null) {
                logger.error("用户角色信息不存在");
                throw new BusinessException(ResultCode.NOT_FOUND, "角色不存在");
            }
            Map<String, Object> map = MapUtil.playerToMap(userPlayerInfo);
            redisUtil.hashPutAll(key,map);
        }
        logger.info("查询了一次用户角色详情");
        return userPlayerInfo;
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
                            Map<String,Object> map = MapUtil.playerToMap(userPlayerInfo);
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
    @Override
    @Transactional
    public void updatePlayerBySave(SaveInfo saveInfo) {
        if (saveInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        UserPlayerInfo userPlayerInfo = saveToPlayer(saveInfo);
        updatePlayerInfo(userPlayerInfo);
    }

    private UserPlayerInfo saveToPlayer(SaveInfo saveInfo) {
        if (saveInfo == null) {
            logger.error("存档读取异常: 空对象");
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        UserPlayerInfo userPlayerInfo = getPlayerInfo();
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
            PlayerService playerService2 = new PlayerServiceImpl();
            UserPlayerInfo userPlayerInfo = playerService2.getPlayerInfo();
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
        UserPlayerInfo userPlayerInfo = getPlayerInfo();
        if(!Objects.equals(userPlayerInfo.getUserId(), userId)){
            throw new BusinessException(ResultCode.UNAUTHORIZED,"非法操作");
        }
        LevelInfo levelInfo = levelService.getLevelInfo(userPlayerInfo.getPlayerClass(), 1);
        userPlayerInfo.setExp(0L);
        userPlayerInfo.setLevel(levelInfo.getLevel());
        userPlayerInfo.setAttackBase(levelInfo.getAttackBase());
        userPlayerInfo.setCurrentHp(levelInfo.getMaxHp());
        userPlayerInfo.setCurrentMp(levelInfo.getMaxMp());
        userPlayerInfo.setUpdateTime(LocalDateTime.now());
        updatePlayerInfo(userPlayerInfo);
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
        UserPlayerInfo userPlayerInfo = getPlayerInfo();
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
}
