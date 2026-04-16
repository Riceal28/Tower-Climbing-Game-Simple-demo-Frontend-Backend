package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.ResultCode;
import com.szm.demo.context.GameContext;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BattleServiceImpl implements BattleService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    TowerService towerService;

    @Autowired
    SaveService saveService;

    @Autowired
    PlayerService playerService;

    @Override
    @Transactional
    public void battleInit(SaveInfo saveInfo) {
        Long saveId = GameContext.getSaveId();
        if (saveId == null) {
            throw new BusinessException(ResultCode.PRECONDITION_FAILED);
        }
        try {
            UserPlayerInfo userPlayerInfo = playerService.getPlayerInfo();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("存档ID[{}]初始化战斗失败", saveId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
