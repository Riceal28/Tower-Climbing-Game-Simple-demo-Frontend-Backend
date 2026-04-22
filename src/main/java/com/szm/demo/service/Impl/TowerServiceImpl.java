package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.entity.TowerFloorInfo;
import com.szm.demo.entity.TowerFloorMonsterInfo;
import com.szm.demo.mapper.TowerMapper;
import com.szm.demo.service.TowerService;
import com.szm.demo.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class TowerServiceImpl implements TowerService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    TowerMapper towerMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override//todo:设置过期时间
    public TowerFloorInfo getBaseByFloor(Integer floor) {
        if (floor == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            String key = RedisKeyConstants.TOWER_BASE.getKey(floor);
            TowerFloorInfo towerFloorInfo = redisUtil.get(key, TowerFloorInfo.class);
            if (towerFloorInfo == null) {
                towerFloorInfo = towerMapper.getBaseByFloor(floor);
                if (towerFloorInfo == null) {
                    throw new BusinessException(ResultCode.NOT_FOUND, "未配置该楼层基本信息");
                }
                redisUtil.set(key, towerFloorInfo);
            }
            return towerFloorInfo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询魔塔楼层[{}]基本信息失败", floor, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override//todo:设置过期时间
    public List<TowerFloorMonsterInfo> getDetailByFloor(Integer floor) {
        if (floor == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            String keySet = RedisKeyConstants.TOWER_LIST.getKey(floor);
            Set<String> orderSet = redisUtil.setMembers(keySet, String.class);
            List<TowerFloorMonsterInfo> towerFloorMonsterInfoList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(orderSet)) {
                for (String order : orderSet) {
                    String detailKey = RedisKeyConstants.TOWER_DETAIL.getKey(floor, order);
                    TowerFloorMonsterInfo t = redisUtil.get(detailKey, TowerFloorMonsterInfo.class);
                    if (t != null) {
                        towerFloorMonsterInfoList.add(t);
                    }
                }
            }
            if (!towerFloorMonsterInfoList.isEmpty()) {
                return towerFloorMonsterInfoList;
            }
            towerFloorMonsterInfoList = towerMapper.getAllDetailByFloor(floor);
            if (towerFloorMonsterInfoList.isEmpty()) {
                throw new BusinessException(ResultCode.NOT_FOUND, "未配置该楼层详细信息");
            }
            for (TowerFloorMonsterInfo tfm : towerFloorMonsterInfoList) {
                Integer tfmOrder = tfm.getBattleOrder();
                String keySet2 = RedisKeyConstants.TOWER_LIST.getKey(floor);
                redisUtil.setAdd(keySet2, tfmOrder);
                String detailKey2 = RedisKeyConstants.TOWER_DETAIL.getKey(floor, tfmOrder);
                redisUtil.set(detailKey2, tfm);
            }
            return towerFloorMonsterInfoList;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询魔塔楼层[{}]详细信息列表失败", floor, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override//todo:设置过期时间
    public TowerFloorMonsterInfo getOneDetailByOrder(Integer floor, Integer battleOrder) {
        if (floor == null || battleOrder == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            String key = RedisKeyConstants.TOWER_DETAIL.getKey(floor, battleOrder);
            TowerFloorMonsterInfo towerFloorMonsterInfo =
                    redisUtil.get(key, TowerFloorMonsterInfo.class);
            if (towerFloorMonsterInfo != null) {
                return towerFloorMonsterInfo;
            }
            logger.info("查询楼层信息floor[{}],order[{}]",floor,battleOrder);
            towerFloorMonsterInfo = towerMapper.getOneDetailByOrder(floor, battleOrder);
            if (towerFloorMonsterInfo == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "未配置该楼层及序号详细信息");
            }
            String keySet = RedisKeyConstants.TOWER_LIST.getKey(floor);
            redisUtil.setAdd(keySet, battleOrder);
            redisUtil.set(key, towerFloorMonsterInfo);
            return towerFloorMonsterInfo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("查询魔塔楼层[{}][{}]详细信息失败", floor, battleOrder, e);
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
    }

    @Override
    public Boolean hasNextBattle(Integer currentFloor, Integer currentOrder) {
        if (currentFloor == null || currentOrder == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            return getOneDetailByOrder(currentFloor, currentOrder + 1) != null;
        } catch (BusinessException e) {
            return false;
        } catch (Exception e) {
            logger.error("判断下一战斗节点[{}][{}]失败", currentFloor, currentOrder + 1, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }

    @Override
    public Boolean hasNextFloor(Integer currentFloor) {
        if (currentFloor == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        try {
            return getBaseByFloor(currentFloor + 1) != null;
        } catch (BusinessException e) {
            return false;
        } catch (Exception e) {
            logger.error("判断下一楼层[{}]失败", currentFloor + 1, e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
    }
}
