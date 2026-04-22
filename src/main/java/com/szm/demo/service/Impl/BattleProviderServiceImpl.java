package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.entity.*;
import com.szm.demo.mapper.BattleInfoMapper;
import com.szm.demo.mapper.MonsterActionInfoMapper;
import com.szm.demo.mapper.PlayerActionInfoMapper;
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

import java.time.LocalDateTime;

@Service
public class BattleProviderServiceImpl implements BattleProviderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    BattleInfoMapper battleInfoMapper;
    @Autowired
    PlayerActionInfoMapper playerActionInfoMapper;
    @Autowired
    MonsterActionInfoMapper monsterActionInfoMapper;
    @Autowired
    SaveService saveService;
    @Autowired
    MonsterService monsterService;
    @Autowired
    TowerService towerService;
    @Autowired
    PlayerService playerService;
    @Autowired
    PlayerProviderService playerProviderService;
    @Autowired
    private SaveProviderService saveProviderService;

    @Override
    @Transactional
    public void updateBattle(BattleInfo battleInfo) {
        if (battleInfo == null) throw new BusinessException(ResultCode.BAD_REQUEST);
        try {
            battleInfoMapper.updateById(battleInfo);
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            String key = RedisKeyConstants.BATTLE_INFO.getKey(battleInfo.getSaveId());
                            redisUtil.hashPutAll(key, MapUtil.battleToMap(battleInfo));
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("更新战斗信息失败,战斗ID[{}]", battleInfo.getId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    /**
     * 战斗结算
     * @return 是否成功结算(仅WIN时返回true, LOSE时返回false)
     */
    @Override
    @Transactional
    public Boolean settleBattle(BattleInfo battleInfo, String result) {
        SaveInfo saveInfo = saveService.getSaveById();
        logger.info("saveInfo{}", saveInfo);

        if ("WIN".equals(result)) {
            // 胜利：保存存档 + 更新角色属性
            updateSaveAfterWin(saveInfo, battleInfo);
            updatePlayerAfterWin(battleInfo);
            return true;
        } else {
            // 失败：仅更新角色HP为0，不修改存档
            updatePlayerAfterLose();
            return false;
        }
    }

    /**
     * 清理战斗状态(始终执行)
     */
    @Override
    @Transactional
    public void cleanupBattle(Long battleId, Long saveId) {
        deleteBattleActions(battleId);
        battleInfoMapper.deleteBySaveId(saveId);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        redisUtil.delete(RedisKeyConstants.BATTLE_INFO.getKey(saveId));
                    }
                }
        );
    }

    /**
     * 战斗胜利后保存存档
     */
    private void updateSaveAfterWin(SaveInfo saveInfo, BattleInfo battleInfo) {
        // 获取经验奖励和进度奖励
        MonsterInfo monsterInfo = monsterService.getByMId(battleInfo.getMonsterId());
        Long gainExp = monsterInfo.getGainExp() != null ? monsterInfo.getGainExp() : 0L;
        Integer rewardProgress = 0;

        TowerFloorMonsterInfo floorMonster = towerService.getOneDetailByOrder(saveInfo.getFloor(), saveInfo.getBattleOrder());
        if (floorMonster != null && floorMonster.getRewardProgress() != null) {
            rewardProgress = floorMonster.getRewardProgress();
        }

        // 更新存档
        saveInfo.setCurrentHp(battleInfo.getPlayerCurrentHp());
        saveInfo.setCurrentMp(battleInfo.getPlayerCurrentMp());
        saveInfo.setBattleOrder(saveInfo.getBattleOrder());
        saveInfo.setProgress(saveInfo.getProgress() + rewardProgress);
        saveInfo.setUpdateTime(LocalDateTime.now());

        // 检查是否需要进入下一层
        TowerFloorInfo towerFloor = towerService.getBaseByFloor(saveInfo.getFloor());
        if (towerFloor != null && saveInfo.getProgress() >= towerFloor.getProgressNeeded()) {
            if (towerService.hasNextFloor(saveInfo.getFloor())) {
                saveInfo.setFloor(saveInfo.getFloor() + 1);
                saveInfo.setBattleOrder(0);
                saveInfo.setProgress(0);
            }
        }
        saveProviderService.updateSave(saveInfo);
    }

    /**
     * 战斗胜利后更新角色属性
     */
    private void updatePlayerAfterWin(BattleInfo battleInfo) {
        UserPlayerInfo player = playerProviderService.getPlayerInfo();
        MonsterInfo monsterInfo = monsterService.getByMId(battleInfo.getMonsterId());
        Long gainExp = monsterInfo.getGainExp() != null ? monsterInfo.getGainExp() : 0L;

        player.setExp(player.getExp() + gainExp);
        player.setCurrentHp(battleInfo.getPlayerCurrentHp());
        player.setCurrentMp(battleInfo.getPlayerCurrentMp());
        playerProviderService.updatePlayerInfo(player);
    }

    /**
     * 战斗失败后更新角色属性
     */
    private void updatePlayerAfterLose() {
        UserPlayerInfo player = playerProviderService.getPlayerInfo();
        player.setCurrentHp(0);
        playerProviderService.updatePlayerInfo(player);
    }
    /**
     * 清理战斗相关技能数据
     */
    private void deleteBattleActions(Long battleId) {
        playerActionInfoMapper.deleteByBattleId(battleId);
        monsterActionInfoMapper.deleteByBattleId(battleId);
        redisUtil.delete(RedisKeyConstants.PLAYER_ACTION.getKey(battleId));
        redisUtil.delete(RedisKeyConstants.MONSTER_ACTION.getKey(battleId));
    }
}
