package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.dto.BattleResp;
import com.szm.demo.entity.*;
import com.szm.demo.mapper.*;
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
import java.util.*;
import java.util.stream.Collectors;

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
    TowerService towerService;
    @Autowired
    ActionService actionService;
    @Autowired
    MonsterService monsterService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LevelService levelService;
    @Autowired
    private PlayerActionInfoMapper playerActionInfoMapper;
    @Autowired
    private MonsterActionInfoMapper monsterActionInfoMapper;
    @Autowired
    private SaveInfoMapper saveInfoMapper;
    @Autowired
    private PlayerProviderService playerProviderService;

    // ==================== 原有方法 ====================

    @Override
    @Transactional
    public BattleInfo create(SaveInfo saveInfo) {
        if (saveInfo == null) throw new BusinessException(ResultCode.BAD_REQUEST);
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
                            redisUtil.hashPutAll(key, MapUtil.battleToMap(battleInfo));
                        }
                    }
            );
            return battleInfo;
        } catch (Exception e) {
            logger.error("插入战斗信息失败,存档ID[{}]", saveInfo.getId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public BattleInfo getBySaveId() {
        Long saveId = GameContext.getSaveId();
        if (saveId == null) throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        try {
            String key = RedisKeyConstants.BATTLE_INFO.getKey(saveId);
            Map<String, Object> map = redisUtil.hashEntries(key, Object.class);
            if (!CollectionUtils.isEmpty(map)) {
                return MapUtil.mapToBattle(map);
            }
            BattleInfo battleInfo = battleInfoMapper.getBySaveId(saveId);
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
        if (saveInfo == null) throw new BusinessException(ResultCode.BAD_REQUEST);
        BattleInfo battleInfo = getBySaveId();
        if (battleInfo == null) throw new BusinessException(ResultCode.BAD_REQUEST);
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

    @Override
    public void afterOneAction(BattleInfo battleInfo, ActionInfo actionInfo) {
        if (battleInfo == null || actionInfo == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        int forHp = actionInfo.getForHp();
        int forMp = actionInfo.getForMp();
        int forDefend = actionInfo.getForDefend();
        int mpCost = actionInfo.getMpCost();

        if (actionInfo.getIsTargetPlayer()) {
            // 自身增益：治疗/MP消耗/护盾
            if (mpCost > battleInfo.getPlayerCurrentMp()) {
                throw new BusinessException(ResultCode.MP_NOT_ENOUGH);
            }
            UserPlayerInfo player = playerProviderService.getPlayerInfo();
            LevelInfo levelInfo = levelService.getLevelInfo(player.getPlayerClass(), player.getLevel());

            battleInfo.setPlayerCurrentHp(Math.max(0, battleInfo.getPlayerCurrentHp() + forHp));
            battleInfo.setPlayerCurrentMp(Math.max(0, battleInfo.getPlayerCurrentMp() + forMp - mpCost));
            battleInfo.setPlayerCurrentDefend(Math.max(0, battleInfo.getPlayerCurrentDefend() + forDefend));

            // 不超过上限
            battleInfo.setPlayerCurrentHp(Math.min(levelInfo.getMaxHp(), battleInfo.getPlayerCurrentHp()));
            battleInfo.setPlayerCurrentMp(Math.min(levelInfo.getMaxMp(), battleInfo.getPlayerCurrentMp()));
            updateBattle(battleInfo);
        } else {
            // 攻击魔物
            if (mpCost > battleInfo.getPlayerCurrentMp()) {
                throw new BusinessException(ResultCode.MP_NOT_ENOUGH);
            }
            UserPlayerInfo player = playerProviderService.getPlayerInfo();
            // 总伤害 = 技能基础伤害 + 角色攻击力
            int totalDamage = Math.abs(forHp) + player.getAttackBase();

            // 先扣格挡值，再扣HP
            int currentDefend = battleInfo.getMonsterCurrentDefend();
            int absorb = Math.min(totalDamage, currentDefend);
            int remainingDamage = totalDamage - absorb;

            battleInfo.setMonsterCurrentDefend(currentDefend - absorb);
            battleInfo.setMonsterCurrentHp(Math.max(0, battleInfo.getMonsterCurrentHp() - remainingDamage));
            battleInfo.setMonsterCurrentMp(battleInfo.getMonsterCurrentMp() + forMp);
            battleInfo.setPlayerCurrentMp(battleInfo.getPlayerCurrentMp() - mpCost);
            updateBattle(battleInfo);
        }
    }

    // ==================== 新增：战斗闭环方法 ====================

    /**
     * 开始战斗：创建战斗实例，绑定楼层怪物与双方技能
     */
    @Override
    @Transactional
    public BattleResp startBattle() {
        SaveInfo saveInfo = saveService.getSaveById();

        // 获取当前楼层遇敌节点
        TowerFloorMonsterInfo floorMonster = towerService.getOneDetailByOrder(saveInfo.getFloor(), saveInfo.getBattleOrder());
        if (floorMonster == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "当前楼层无更多敌人");
        }

        // 获取怪物信息
        MonsterInfo monsterInfo = monsterService.getByMId(floorMonster.getMonsterId());

        // 创建或重置战斗
        BattleInfo battleInfo;
        if (saveInfo.getBattleOrder() == 0) {
            battleInfo = create(saveInfo);
        } else {
            battleInfo = getBySaveId();
            if (battleInfo == null) {
                battleInfo = create(saveInfo);
            } else {
                battleInfo = convertFromSave(saveInfo);
            }

        }

        // 初始化玩家状态（从存档恢复HP/MP）
        battleInfo.setPlayerCurrentHp(saveInfo.getCurrentHp());
        battleInfo.setPlayerCurrentMp(saveInfo.getCurrentMp());
        battleInfo.setPlayerCurrentDefend(0);

        // 初始化魔物状态
        battleInfo.setMonsterId(monsterInfo.getId());
        battleInfo.setMonsterCurrentHp(monsterInfo.getHp());
        battleInfo.setMonsterCurrentMp(monsterInfo.getMp());
        battleInfo.setMonsterCurrentDefend(0);
        updateBattle(battleInfo);

        // 绑定玩家技能到战斗
        bindPlayerActions(battleInfo.getId());

        // 绑定魔物技能
        bindMonsterActions(battleInfo.getId(), monsterInfo.getId());

        return new BattleResp(battleInfo, monsterInfo, "战斗开始！遭遇 " + monsterInfo.getMonsterName(), null);
    }

    /**
     * 玩家使用技能
     */
    @Override
    public BattleResp playerAction(Long actionId) {
        BattleInfo battleInfo = getBySaveId();
        validateBattleActive(battleInfo);

        // 获取技能定义和玩家技能实例
        ActionInfo actionInfo = actionService.getActionByAId(actionId);
        PlayerActionInfo pa = getPaByActionId(actionId);

        // 校验技能可用
        if (pa == null) throw new BusinessException(ResultCode.SKILL_NOT_OWNED);
        if (pa.getCurrentCd() > 0) throw new BusinessException(ResultCode.SKILL_ON_COOLDOWN);
        if (actionInfo.getMpCost() > battleInfo.getPlayerCurrentMp()) {
            throw new BusinessException(ResultCode.MP_NOT_ENOUGH);
        }

        // 记录日志
        String log = buildPlayerActionLog(actionInfo, battleInfo);

        // 执行技能效果
        afterOneAction(battleInfo, actionInfo);

        // 更新技能CD和持续回合
        pa.setCurrentCd(actionInfo.getCd());
        pa.setRestContinueRound(actionInfo.getIsContinue() ? actionInfo.getContinueRound() : 0);
        actionService.updatePaOne(pa);

        // 刷新战斗信息
        battleInfo = getBySaveId();
        MonsterInfo monsterInfo = monsterService.getByMId(battleInfo.getMonsterId());

        // 检查胜负
        String result = checkBattleEnd(battleInfo);
        if (result != null) {
            //todo: 自调用导致事务注解失效, 粗略解决方法改进
            BattleService battleService2 = new BattleServiceImpl();
            battleService2.settleBattle(battleInfo, result);
        }

        return new BattleResp(battleInfo, monsterInfo, log, result);
    }

    /**
     * 结束回合：魔物行动 + 回合结束处理
     */
    @Override
    public BattleResp endRound() {
        BattleInfo battleInfo = getBySaveId();
        validateBattleActive(battleInfo);

        StringBuilder logBuilder = new StringBuilder();

        // 魔物AI：随机选择可用技能
        List<MonsterActionInfo> monsterActions = monsterActionInfoMapper.getByBattleId(battleInfo.getId());
        List<MonsterActionInfo> available = monsterActions.stream()
                .filter(m -> m.getCurrentCd() <= 0)
                .collect(Collectors.toList());

        if (!available.isEmpty()) {
            MonsterActionInfo chosen = available.get(new Random().nextInt(available.size()));
            ActionInfo actionInfo = actionService.getActionByAId(chosen.getActionId());
            String monsterLog = monsterAction(actionInfo, battleInfo, chosen);
            logBuilder.append(monsterLog);

            // 更新魔物技能CD
            chosen.setCurrentCd(actionInfo.getCd());
            actionService.updateMaOne(chosen);
        } else {
            logBuilder.append("魔物待机。");
        }

        // 回合结束：所有技能CD和持续回合-1
        //todo:actionService添加相应代码
        List<PlayerActionInfo> playerActions = playerActionInfoMapper.getByBattleId(battleInfo.getId());
        actionService.passRoundAllPaUpdate(playerActions);
        actionService.passRoundAllMaUpdate(monsterActions);

        // 刷新战斗状态
        battleInfo = getBySaveId();
        MonsterInfo monsterInfo = monsterService.getByMId(battleInfo.getMonsterId());

        // 检查胜负
        String result = checkBattleEnd(battleInfo);
        if (result != null) {
            //todo: 自调用导致事务注解失效, 粗略解决方法改进
            BattleService battleService2 = new BattleServiceImpl();
            battleService2.settleBattle(battleInfo, result);
            logBuilder.append(" 战斗结束：").append(result.equals("WIN") ? "胜利！" : "失败...");
        }

        return new BattleResp(battleInfo, monsterInfo, logBuilder.toString(), result);
    }

    /**
     * 查询当前战斗状态
     */
    @Override
    public BattleResp getStatus() {
        BattleInfo battleInfo = getBySaveId();
        if (battleInfo == null || battleInfo.getMonsterId() == 0) {
            throw new BusinessException(ResultCode.BATTLE_NOT_FOUND);
        }
        MonsterInfo monsterInfo = monsterService.getByMId(battleInfo.getMonsterId());
        return new BattleResp(battleInfo, monsterInfo, null, null);
    }

    /**
     * 检查胜负：null=继续，WIN=胜利，LOSE=失败
     */
    @Override
    public String checkBattleEnd(BattleInfo battleInfo) {
        if (battleInfo.getPlayerCurrentHp() <= 0) return "LOSE";
        if (battleInfo.getMonsterCurrentHp() <= 0) return "WIN";
        return null;
    }

    /**
     * 战斗结算
     */
    @Override
    @Transactional
    public void settleBattle(BattleInfo battleInfo, String result) {
        Long saveId = GameContext.getSaveId();
        SaveInfo saveInfo = saveService.getSaveById();

        if ("WIN".equals(result)) {
            // 胜利：获取经验奖励和进度奖励
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
            saveInfo.setBattleOrder(saveInfo.getBattleOrder() + 1);
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
            saveService.updateSave(saveInfo);

            // 更新角色属性（等级和经验）
            UserPlayerInfo player = playerProviderService.getPlayerInfo();
            player.setExp(player.getExp() + gainExp);
            player.setCurrentHp(battleInfo.getPlayerCurrentHp());
            player.setCurrentMp(battleInfo.getPlayerCurrentMp());
            playerProviderService.updatePlayerInfo(player);

            // 尝试升级
            playerService.tryLevelUp();

        } else {
            // 失败：角色HP设为0，但不修改存档
            UserPlayerInfo player = playerProviderService.getPlayerInfo();
            player.setCurrentHp(0);
            playerProviderService.updatePlayerInfo(player);
        }

        // 清理战斗状态
        deleteBattleActions(battleInfo.getId());
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

    // ==================== 私有辅助方法 ====================

    /**
     * 绑定玩家技能到战斗
     */
    private void bindPlayerActions(Long battleId) {
        Long playerId = GameContext.getPlayerId();
        List<PlayerActionInfo> actions = playerActionInfoMapper.getByBattleId(0L);
        if (actions.isEmpty()) return;

        for (PlayerActionInfo pa : actions) {
            pa.setBattleId(battleId);
        }
        playerActionInfoMapper.updateBattleIdBatch(actions);
    }

    /**
     * 绑定魔物技能到战斗
     */
    private void bindMonsterActions(Long battleId, Long monsterId) {
        List<MonsterActionInfo> monsterActions = monsterService.getMonsterActions(monsterId);
        if (monsterActions == null || monsterActions.isEmpty()) return;

        for (MonsterActionInfo ma : monsterActions) {
            ma.setBattleId(battleId);
        }
        monsterActionInfoMapper.updateBattleIdBatch(monsterActions);
    }

    /**
     * 根据技能ID获取玩家技能实例
     */
    private PlayerActionInfo getPaByActionId(Long actionId) {
        Long battleId = GameContext.getBattleId();
        List<PlayerActionInfo> actions = playerActionInfoMapper.getByBattleId(battleId);
        return actions.stream()
                .filter(a -> Objects.equals(a.getActionId(), actionId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 校验战斗是否进行中
     */
    private void validateBattleActive(BattleInfo battleInfo) {
        if (battleInfo == null) throw new BusinessException(ResultCode.BATTLE_NOT_FOUND);
        if (battleInfo.getMonsterId() == null || battleInfo.getMonsterId() == 0) {
            throw new BusinessException(ResultCode.BATTLE_NOT_FOUND);
        }
        if (battleInfo.getPlayerCurrentHp() <= 0) {
            throw new BusinessException(ResultCode.PLAYER_DEAD);
        }
        if (battleInfo.getMonsterCurrentHp() <= 0) {
            throw new BusinessException(ResultCode.MONSTER_DEAD);
        }
    }

    /**
     * 构建玩家行动日志
     */
    private String buildPlayerActionLog(ActionInfo actionInfo, BattleInfo battleInfo) {
        String actionName = actionInfo.getActionName();
        if (actionInfo.getIsTargetPlayer()) {
            return "使用了「" + actionName + "」";
        } else {
            int damage = Math.abs(actionInfo.getForHp()) + playerProviderService.getPlayerInfo().getAttackBase();
            return "使用了「" + actionName + "」，造成 " + damage + " 点伤害";
        }
    }

    /**
     * 魔物行动
     */
    private String monsterAction(ActionInfo actionInfo, BattleInfo battleInfo, MonsterActionInfo ma) {
        int forHp = actionInfo.getForHp();
        int forMp = actionInfo.getForMp();
        int forDefend = actionInfo.getForDefend();
        MonsterInfo monsterInfo = monsterService.getByMId(battleInfo.getMonsterId());

        if (actionInfo.getIsTargetPlayer()) {
            // 魔物增益
            battleInfo.setMonsterCurrentMp(battleInfo.getMonsterCurrentMp() + forMp);
            battleInfo.setMonsterCurrentDefend(Math.max(0, battleInfo.getMonsterCurrentDefend() + forDefend));
            return monsterInfo.getMonsterName() + " 使用了「" + actionInfo.getActionName() + "」";
        } else {
            // 魔物攻击玩家
            int totalDamage = Math.abs(forHp) + monsterInfo.getAttackBase();
            int currentDefend = battleInfo.getPlayerCurrentDefend();
            int absorb = Math.min(totalDamage, currentDefend);
            int remainingDamage = totalDamage - absorb;

            battleInfo.setPlayerCurrentDefend(currentDefend - absorb);
            battleInfo.setPlayerCurrentHp(Math.max(0, battleInfo.getPlayerCurrentHp() - remainingDamage));
            battleInfo.setMonsterCurrentMp(battleInfo.getMonsterCurrentMp() + forMp);
            updateBattle(battleInfo);

            return monsterInfo.getMonsterName() + " 使用了「" + actionInfo.getActionName() + "」，造成 " + remainingDamage + " 点伤害";
        }
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
