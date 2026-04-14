package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.ResultCode;
import com.szm.demo.entity.SaveInfo;
import com.szm.demo.entity.UserPlayerInfo;
import com.szm.demo.service.BattleService;
import com.szm.demo.service.SaveService;
import com.szm.demo.service.TowerService;
import com.szm.demo.service.UserService;
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
    UserService userService;

    @Override
    @Transactional
    public void battleInit(SaveInfo saveInfo) {
        if (saveId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            UserPlayerInfo userPlayerInfo = userService.getPlayerInfo(userId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("用户[{}]初始化战斗失败", userId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
