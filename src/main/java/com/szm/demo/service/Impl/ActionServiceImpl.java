package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.dto.ActionDetailResp;
import com.szm.demo.entity.ActionInfo;
import com.szm.demo.entity.MonsterActionInfo;
import com.szm.demo.entity.PlayerActionGroup;
import com.szm.demo.entity.PlayerActionInfo;
import com.szm.demo.mapper.ActionInfoMapper;
import com.szm.demo.mapper.MonsterActionInfoMapper;
import com.szm.demo.mapper.PlayerActionGroupMapper;
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
import java.util.*;

@Service
public class ActionServiceImpl implements ActionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    ActionInfoMapper actionInfoMapper;

    @Autowired
    PlayerActionInfoMapper playerActionInfoMapper;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MonsterActionInfoMapper monsterActionInfoMapper;
    @Autowired
    private PlayerActionGroupMapper playerActionGroupMapper;

    @Override
    @Transactional
    public void addDefaultAction(Long playerId, Integer levelId) {
//        Long playerId = GameContext.getPlayerId();
//        Long battleId = GameContext.getBattleId();
        if (playerId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        List<PlayerActionInfo> playerActionInfoList = new ArrayList<>();
        List<PlayerActionGroup> ag = playerActionGroupMapper.getByLId(levelId);
        for (PlayerActionGroup a : ag) {
            PlayerActionInfo p = new PlayerActionInfo();
            p.setBattleId(0L);
            p.setPlayerId(playerId);
            p.setActionId(a.getActionId());
            p.setCurrentCd(0);
            p.setRestContinueRound(0);
            p.setCreateTime(LocalDateTime.now());
            p.setUpdateTime(LocalDateTime.now());
            playerActionInfoList.add(p);
        }
        try {
            playerActionInfoMapper.batchInsert(playerActionInfoList);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
//                            Map<String, Object> batch = new HashMap<>();
//                            String key = RedisKeyConstants.PLAYER_ACTION.getKey(battleId);
//                            for (PlayerActionInfo p : playerActionInfoList) {
//                                Map<String, Object> map = MapUtil.paToMap(p);
//                                batch.put(p.getId().toString(), map);
//                            }
//                            redisUtil.hashPutAll(key, batch);
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
    public PlayerActionInfo getPaById(Long id) {
        Long battleId = GameContext.getBattleId();
        if (battleId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        if (id == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            String key = RedisKeyConstants.PLAYER_ACTION.getKey(battleId);
            Map<String, Object> map = redisUtil.hashGet(key, id.toString(), Map.class);
            if (!map.isEmpty()) {
                return MapUtil.mapToPa(map);
            }
            PlayerActionInfo pa = playerActionInfoMapper.getById(id);
            if (pa == null) {
                throw new BusinessException(ResultCode.NOT_FOUND);
            }
            map = MapUtil.paToMap(pa);
            redisUtil.hashPut(key, id.toString(), map);
            return pa;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询角色技能ID[{}]失败", id, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public List<PlayerActionInfo> getPaByBId(Long battleId) {
        if (battleId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        try {
            String key = RedisKeyConstants.PLAYER_ACTION.getKey(battleId);
            Map<String, Map> map = redisUtil.hashEntries(key, Map.class);
            if (!map.isEmpty()) {
                List<PlayerActionInfo> list = new ArrayList<>();
                map.forEach((id, m) -> {
                    if(!m.isEmpty()){
                        PlayerActionInfo pa = MapUtil.mapToPa(m);
                        logger.info("缓存:根据战斗ID获取角色技能组[{}]",pa);
                        list.add(pa);
                    }
                });
            }
            List<PlayerActionInfo> list = playerActionInfoMapper.getByBattleId(battleId);
            logger.info("DB:根据战斗ID获取角色技能组[{}]",list);
            if (list.isEmpty()) {
                throw new BusinessException(ResultCode.NOT_FOUND);
            }
            Map<String, Object> batch = new HashMap<>();
            for (PlayerActionInfo p : list) {
                batch.put(battleId.toString(), MapUtil.paToMap(p));
            }
            redisUtil.hashPutAll(key, batch);
            return list;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询战斗技能列表失败,战斗ID[{}]", battleId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public List<ActionDetailResp> getAllActionResp(){
        Long battleId = GameContext.getBattleId();
        if(battleId ==null){
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        List<PlayerActionInfo> paList = getPaByBId(battleId);
        List<ActionInfo> actionInfoList = new ArrayList<>();
        List<ActionDetailResp> acs = new ArrayList<>();
        for(PlayerActionInfo pa : paList){
            ActionInfo actionInfo = getActionByAId(pa.getActionId());
            if(actionInfo!=null){
                actionInfoList.add(actionInfo);
                ActionDetailResp ac = new ActionDetailResp();
                ac.setActionId(pa.getActionId());
                ac.setActionType(actionInfo.getActionType());
                ac.setActionName(actionInfo.getActionName());
                ac.setDescription(actionInfo.getDescription());
                ac.setForHp(actionInfo.getForHp());
                ac.setForMp(actionInfo.getForMp());
                ac.setForDefend(actionInfo.getForDefend());
                ac.setMpCost(actionInfo.getMpCost());
                ac.setTargetPlayer(actionInfo.getIsTargetPlayer());
                ac.setCurrentCd(pa.getCurrentCd());
                ac.setRestContinueRound(pa.getRestContinueRound());
                acs.add(ac);
            }
        }
        if(acs.isEmpty()){
            return Collections.emptyList();
        }
        return acs;
    }


    @Override
    @Transactional
    public void updatePaOne(PlayerActionInfo p) {
        Long battleId = GameContext.getBattleId();
        if (battleId == null) {
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
                            String key = RedisKeyConstants.PLAYER_ACTION.getKey(battleId);
                            Map<String, Object> map = new HashMap<>();
                            map.put("currentCd", p.getCurrentCd());
                            map.put("restContinueRound", p.getRestContinueRound());
                            map.put("updateTime", p.getUpdateTime());
                            redisUtil.hashPut(key, p.getId().toString(), map);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("更新角色技能情况失败,战斗ID[{}]", battleId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    public void updatePaAll(List<PlayerActionInfo> list) {
        Long battleId = GameContext.getBattleId();
        if (battleId == null) {
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
                            String key = RedisKeyConstants.PLAYER_ACTION.getKey(battleId);
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
            logger.error("批量更新角色技能情况失败,战斗ID[{}]", battleId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    public void passRoundOnePaUpdate(PlayerActionInfo playerActionInfo) {
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
    public void passRoundAllPaUpdate(List<PlayerActionInfo> list) {
        if (list.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        for (PlayerActionInfo p : list) {
            p.setCurrentCd(Math.max(0, p.getCurrentCd() - 1));
            p.setRestContinueRound(Math.max(0, p.getRestContinueRound() - 1));
        }
        updatePaAll(list);
    }

    //todo:提取公共方法
    @Override
    public MonsterActionInfo getMaById(Long id) {
        Long battleId = GameContext.getBattleId();
        if (battleId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        if (id == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            String key = RedisKeyConstants.MONSTER_ACTION.getKey(battleId);
            Map<String, Object> map = redisUtil.hashGet(key, id.toString(), Map.class);
            if (!map.isEmpty()) {
                return MapUtil.mapToMa(map);
            }
            MonsterActionInfo ma = monsterActionInfoMapper.getById(id);
            if (ma == null) {
                throw new BusinessException(ResultCode.NOT_FOUND);
            }
            map = MapUtil.maToMap(ma);
            redisUtil.hashPut(key, id.toString(), map);
            return ma;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询魔物技能ID[{}]失败", id, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }


    @Override
    @Transactional
    public void updateMaOne(MonsterActionInfo m) {
        Long battleId = GameContext.getBattleId();
        if (battleId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        if (m == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            monsterActionInfoMapper.updateOne(m);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.MONSTER_ACTION.getKey(battleId);
                            Map<String, Object> map = new HashMap<>();
                            map.put("currentCd", m.getCurrentCd());
                            map.put("restContinueRound", m.getRestContinueRound());
                            map.put("updateTime", m.getUpdateTime());
                            redisUtil.hashPut(key, m.getId().toString(), map);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("更新魔物技能情况失败,战斗ID[{}]", battleId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    public void updateMaAll(List<MonsterActionInfo> list) {
        Long battleId = GameContext.getBattleId();
        if (battleId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        if (list.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            monsterActionInfoMapper.updateBatch(list);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            Map<String, Object> batch = new HashMap<>();
                            String key = RedisKeyConstants.MONSTER_ACTION.getKey(battleId);
                            for (MonsterActionInfo m : list) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("currentCd", m.getCurrentCd());
                                map.put("restContinueRound", m.getRestContinueRound());
                                map.put("updateTime", m.getUpdateTime());
                                batch.put(m.getId().toString(), map);
                            }
                            redisUtil.hashPutAll(key, batch);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("批量更新魔物技能情况失败,战斗ID[{}]", battleId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    @Transactional
    public void passRoundOneMaUpdate(MonsterActionInfo monsterActionInfo) {
        if (monsterActionInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        int currentCd = monsterActionInfo.getCurrentCd();
        int restRound = monsterActionInfo.getRestContinueRound();
        monsterActionInfo.setCurrentCd(Math.max(0, currentCd - 1));
        monsterActionInfo.setRestContinueRound(Math.max(0, restRound - 1));
        updateMaOne(monsterActionInfo);
    }

    @Override
    @Transactional
    public void passRoundAllMaUpdate(List<MonsterActionInfo> list) {
        if (list.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        for (MonsterActionInfo m : list) {
            m.setCurrentCd(Math.max(0, m.getCurrentCd() - 1));
            m.setRestContinueRound(Math.max(0, m.getRestContinueRound() - 1));
        }
        updateMaAll(list);
    }
}
