package com.szm.demo.service.Impl;

import com.szm.demo.common.BusinessException;
import com.szm.demo.common.RedisKeyConstants;
import com.szm.demo.common.ResultCode;
import com.szm.demo.entity.LevelInfo;
import com.szm.demo.entity.UserDetail;
import com.szm.demo.mapper.LevelInfoMapper;
import com.szm.demo.mapper.UserDetailMapper;
import com.szm.demo.service.LevelService;
import com.szm.demo.service.UserService;
import com.szm.demo.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class LevelServiceImpl implements LevelService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    UserService userService;

    @Autowired
    LevelInfoMapper levelInfoMapper;

    @Autowired
    UserDetailMapper userDetailMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public LevelInfo getLevelInfo(Integer level) {
        if (level == null || level <= 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST);
        }
        String key = RedisKeyConstants.LEVEL_INFO.getKey(level);
        LevelInfo levelInfo = redisUtil.get(key, LevelInfo.class);
        if (levelInfo == null) {
            levelInfo = levelInfoMapper.getByLevel(level);
            if (levelInfo == null) {
                logger.error("等级配置缺失");
                throw new BusinessException(ResultCode.NOT_FOUND, "等级配置不存在");
            }
            redisUtil.set(key, levelInfo, 1440, TimeUnit.HOURS);
        }
        logger.info("查询了一次等级信息");
        return levelInfo;
    }
    /**
     * 升级,需配合检测经验
     *
     * @param userId   用户ID
     * @param extraExp 多余的经验
     * @return 新等级的经验
     */
    @Override
    @Transactional
    public Long levelUp(Long userId, Long extraExp) {
        UserDetail userDetail = userService.getUserDetail(userId);
        int nextLevel = userDetail.getLevel() + 1;
        if (nextLevel >= 37) {
            userService.setExp(userId,0L);
            return -1L;
        }
        try {
            LevelInfo levelInfo = getLevelInfo(nextLevel);

            userDetail.setLevel(nextLevel);
            userDetail.setExp(extraExp);
            userDetail.setAttackBase(levelInfo.getAttackBase());
            userDetail.setCurrentHp(levelInfo.getMaxHp());
            userDetail.setCurrentMp(levelInfo.getMaxMp());
            userDetail.setUpdateTime(LocalDateTime.now());
            String key = RedisKeyConstants.USER_DETAIL.getKey(userId);
            userDetailMapper.updateAllByUserId(userDetail);

            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            redisUtil.set(key, userDetail, 1440, TimeUnit.MINUTES);
                        }
                    }
            );
        } catch (Exception e) {
            logger.error("用户ID[{}]:升级失败", userDetail.getUserId(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR);
        }
        logger.info("用户ID[{}]:升级成功,当前等级:{},多余经验值:{}",userId,nextLevel,extraExp);
        return extraExp;
    }
}
