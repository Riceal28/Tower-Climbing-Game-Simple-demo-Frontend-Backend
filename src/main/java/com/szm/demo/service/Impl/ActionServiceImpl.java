package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.entity.ActionInfo;
import com.szm.demo.entity.PlayerActionInfo;
import com.szm.demo.mapper.ActionInfoMapper;
import com.szm.demo.mapper.PlayerActionInfoMapper;
import com.szm.demo.service.ActionService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActionServiceImpl implements ActionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ActionInfoMapper actionInfoMapper;

    @Autowired
    PlayerActionInfoMapper playerActionInfoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional
    public void addDefaultAction() {
        Long playerId = GameContext.getPlayerId();
        Long saveId = GameContext.getSaveId();
        if (playerId == null || saveId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
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
        try {
            playerActionInfoMapper.batchInsert(playerActionInfoList);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            for (PlayerActionInfo p : playerActionInfoList) {
                                String key = RedisKeyConstants.PLAYER_ACTION.getKey(playerId, saveId, p.getId());
                                redisUtil.hashPutAll(key, MapUtil.paToMap(p));//todo设置过期时间
                            }
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("角色ID[{}]添加技能组失败", playerId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public ActionInfo getActionByAId(Long actionId) {
        try {
            String key = RedisKeyConstants.ACTION_INFO.getKey(actionId);
            ActionInfo actionInfo = redisUtil.get(key, ActionInfo.class);
            if (actionInfo == null) {
                actionInfo = actionInfoMapper.getByAId(actionId);
                if (actionInfo == null) {
                    throw new BusinessException(ResultCode.NOT_FOUND, "未配置该技能");
                }
                redisUtil.set(key, actionInfo);
            }
            return actionInfo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询技能ID[{}]失败", actionId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }



    @Override
    @Transactional
    public void updatePaOne(PlayerActionInfo p) {
        Long playerId = GameContext.getPlayerId();
        Long saveId = GameContext.getSaveId();
        if (playerId == null || saveId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        if (p == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            playerActionInfoMapper.updateOne(p);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.PLAYER_ACTION.getKey(playerId, saveId, p.getId());
                            Map<String, Object> map = new HashMap<>();
                            map.put("currentCd", p.getCurrentCd());
                            map.put("restContinueRound", p.getRestContinueRound());
                            map.put("updateTime", p.getUpdateTime());
                            redisUtil.hashPutAll(key, map);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("更新角色技能情况失败,存档ID[{}]", saveId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    public void updatePaAll(List<PlayerActionInfo> list) {
        Long playerId = GameContext.getPlayerId();
        Long saveId = GameContext.getSaveId();
        if (playerId == null || saveId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        if (list.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            playerActionInfoMapper.updateBatch(list);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            Map<String, Object> batch = new HashMap<>();
                            String key = RedisKeyConstants.PLAYER_ACTION.getKey(playerId, saveId);
                            for (PlayerActionInfo p : list) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("currentCd", p.getCurrentCd());
                                map.put("restContinueRound", p.getRestContinueRound());
                                map.put("updateTime", p.getUpdateTime());
                                batch.put(p.getId().toString(), map);
                            }
                            redisUtil.hashPutAll(key, batch);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("批量更新角色技能情况失败,存档ID[{}]", saveId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    public void passRoundOneUpdate(PlayerActionInfo playerActionInfo) {
        if (playerActionInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        int currentCd = playerActionInfo.getCurrentCd();
        int restRound = playerActionInfo.getRestContinueRound();
        playerActionInfo.setCurrentCd(Math.max(0, currentCd - 1));
        playerActionInfo.setRestContinueRound(Math.max(0, restRound - 1));
        updatePaOne(playerActionInfo);
    }

    @Override
    @Transactional
    public void passRoundAllUpdate(List<PlayerActionInfo> list) {
        if (list.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        for (PlayerActionInfo p : list) {
            p.setCurrentCd(Math.max(0, p.getCurrentCd() - 1));
            p.setRestContinueRound(Math.max(0, p.getRestContinueRound() - 1));
        }
        updatePaAll(list);
    }
}
