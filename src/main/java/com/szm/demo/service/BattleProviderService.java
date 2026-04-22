package com.szm.demo.service;

import com.szm.demo.entity.BattleInfo;

public interface BattleProviderService {

    void updateBattle(BattleInfo battleInfo);

    /**
     * 战斗结算
     * @param battleInfo 战斗信息
     * @param result 结算结果: WIN/LOSE
     * @return 是否成功结算(仅WIN时返回true, LOSE时返回false)
     */
    Boolean settleBattle(BattleInfo battleInfo, String result);

    /**
     * 清理战斗状态(始终执行)
     */
    void cleanupBattle(Long battleId, Long saveId);
}
