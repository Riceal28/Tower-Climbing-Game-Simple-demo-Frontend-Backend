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
                                //todo: 缓存键有待重新考量
                                String key = RedisKeyConstants.PLAYER_ACTION.getKey(playerId, p.getId());
                                redisUtil.set(key, p);
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
    public ActionInfo getByAId(Long actionId) {
        try {
            String key = RedisKeyConstants.ACTION_INFO.getKey(actionId);
            ActionInfo actionInfo = redisUtil.get(key, ActionInfo.class);
            if (actionInfo == null) {
                actionInfo = actionInfoMapper.getByAId(actionId);
                if(actionInfo==null){
                    throw new BusinessException(ResultCode.NOT_FOUND,"未配置该技能");
                }
                redisUtil.set(key,actionInfo);
            }
            return actionInfo;
        } catch (BusinessException e){
            throw e;
        } catch (Exception e){
            logger.error("查询技能ID[{}]失败",actionId,e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
