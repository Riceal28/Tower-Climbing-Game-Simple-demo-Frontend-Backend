package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.entity.MonsterActionInfo;
import com.szm.demo.entity.MonsterInfo;
import com.szm.demo.mapper.MonsterActionInfoMapper;
import com.szm.demo.mapper.MonsterInfoMapper;
import com.szm.demo.service.MonsterService;
import com.szm.demo.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Service
public class MonsterServiceImpl implements MonsterService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MonsterInfoMapper monsterInfoMapper;
    @Autowired
    MonsterActionInfoMapper monsterActionInfoMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public MonsterInfo getByMId(Long monsterId) {
        if (monsterId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            String key = RedisKeyConstants.MONSTER_INFO.getKey(monsterId);
            MonsterInfo monsterInfo = redisUtil.get(key, MonsterInfo.class);
            if (monsterInfo != null) {
                return monsterInfo;
            }
            monsterInfo = monsterInfoMapper.getByMId(monsterId);
            if (monsterInfo == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "未配置该魔物信息");
            }
            redisUtil.set(key, monsterInfo);
            return monsterInfo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询魔物信息失败,魔物ID[{}]", monsterId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public List<MonsterActionInfo> getMonsterActions(Long monsterId) {
        if (monsterId == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            return monsterActionInfoMapper.getByMonsterId(monsterId);
        } catch (Exception e) {
            logger.error("查询魔物技能失败,魔物ID[{}]", monsterId, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
