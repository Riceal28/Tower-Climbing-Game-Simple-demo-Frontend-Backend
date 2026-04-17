package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.entity.BattleInfo;
import com.szm.demo.entity.MonsterInfo;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.mapper.BattleInfoMapper;
import com.szm.demo.service.*;
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
import java.util.Map;
import java.util.Objects;

@Service
public class BattleServiceImpl implements BattleService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    BattleInfoMapper battleInfoMapper;

    @Autowired
    SaveService saveService;

    @Autowired
    PlayerService playerService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    @Transactional
    public BattleInfo create(SaveInfo saveInfo) {
        if (saveInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            BattleInfo battleInfo = new BattleInfo();
            battleInfo.setSaveId(saveInfo.getId());
            battleInfo.setPlayerCurrentHp(saveInfo.getCurrentHp());
            battleInfo.setPlayerCurrentMp(saveInfo.getCurrentMp());
            battleInfo.setPlayerCurrentDefend(0);
            battleInfo.setMonsterId(0L);
            battleInfo.setMonsterCurrentHp(0);
            battleInfo.setMonsterCurrentMp(0);
            battleInfo.setMonsterCurrentDefend(0);
            battleInfo.setCreateTime(LocalDateTime.now());
            battleInfo.setUpdateTime(LocalDateTime.now());
            battleInfoMapper.insert(battleInfo);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.BATTLE_INFO.getKey(saveInfo.getId());
                            redisUtil.hashPutAll(key, MapUtil.battleToMap(battleInfo));//todo:设置过期时间
                        }
                    }
            );
            return battleInfo;
        } catch (Exception e) {
            logger.error("插入战斗信息失败,存档ID[{}]", saveInfo.getId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override//todo: 设置过期时间
    public BattleInfo getBySaveId() {
        Long saveId = GameContext.getSaveId();
        if (saveId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        try {
            String key = RedisKeyConstants.BATTLE_INFO.getKey(saveId);
            Map<String, Object> map = redisUtil.hashEntries(key, Object.class);
            if (!CollectionUtils.isEmpty(map)) {
                return MapUtil.mapToBattle(map);
            }
            BattleInfo battleInfo = battleInfoMapper.getBySaveId(saveId);
            //允许NULL
            if (battleInfo != null) {
                map = MapUtil.battleToMap(battleInfo);
                redisUtil.hashPutAll(key, map);
            }
            return battleInfo;
        } catch (Exception e) {
            logger.error("查询战斗信息失败,存档ID[{}]", saveId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public BattleInfo convertFromSave(SaveInfo saveInfo) {
        if (saveInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        BattleInfo battleInfo = getBySaveId();
        if (battleInfo == null) {
            logger.error("需要先创建战斗信息");
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        if (!Objects.equals(battleInfo.getSaveId(), saveInfo.getId())) {
            throw new BusinessException(ResultCode.OPERATION_FAILED);
        }
        battleInfo.setPlayerCurrentHp(saveInfo.getCurrentHp());
        battleInfo.setPlayerCurrentMp(saveInfo.getCurrentMp());
        battleInfo.setPlayerCurrentDefend(0);
        battleInfo.setMonsterId(0L);
        battleInfo.setMonsterCurrentHp(0);
        battleInfo.setMonsterCurrentMp(0);
        battleInfo.setMonsterCurrentDefend(0);
        battleInfo.setUpdateTime(LocalDateTime.now());
        return battleInfo;
    }

    @Override
    public void updateBattle(BattleInfo battleInfo) {
        if (battleInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            battleInfoMapper.updateById(battleInfo);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.BATTLE_INFO.getKey(battleInfo.getSaveId());
                            Map<String, Object> map = MapUtil.battleToMap(battleInfo);
                            redisUtil.hashPutAll(key, map);//todo:设置过期时间
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("更新战斗信息失败,战斗ID[{}]", battleInfo.getId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
