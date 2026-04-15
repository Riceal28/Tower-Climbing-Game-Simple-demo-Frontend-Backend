package com.szm.demo.mapper;

import com.szm.demo.entity.UserPlayerInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserPlayerInfoMapper {

    /**
     * 根据ID查询角色信息
     */
    @Select("SELECT * FROM user_player_info WHERE id = #{id}")
    UserPlayerInfo getById(@Param("id") Long id);

    /**
     * 根据用户ID查询用户下所有角色信息
     */
    @Select("SELECT * FROM user_player_info WHERE user_id =#{userId} ORDER BY id ASC")
    List<UserPlayerInfo> getAllByUserId(@Param("userId") Long userId);

    /**
     * 新增用户角色
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO user_player_info (user_id, player_class, level, exp, attack_base, current_hp, " +
            "current_mp, create_time, update_time) " +
            "VALUES (#{userPlayerInfo.userId},#{userPlayerInfo.playerClass},#{userPlayerInfo.level}," +
            "#{userPlayerInfo.exp},#{userPlayerInfo.attackBase},#{userPlayerInfo.currentHp}," +
            "#{userPlayerInfo.currentMp},#{userPlayerInfo.createTime},#{userPlayerInfo.updateTime})")
    void insert(@Param("userPlayerInfo") UserPlayerInfo userPlayerInfo);

    /**
     * 根据ID更新全部信息
     */
    @Update("UPDATE user_player_info SET level=#{userPlayerInfo.level}, exp=#{userPlayerInfo.exp}, " +
            "attack_base=#{userPlayerInfo.attackBase}, current_hp=#{userPlayerInfo.currentHp}, " +
            "current_mp=#{userPlayerInfo.currentMp}, update_time=#{userPlayerInfo.updateTime}" +
            "WHERE id=#{userPlayerInfo.id}")
    int updateAllById(@Param("userPlayerInfo") UserPlayerInfo userPlayerInfo);

    /**
     * 根据ID更新经验
     */
    @Update("UPDATE user_player_info SET exp=#{exp} WHERE id=#{id}")
    int updateExpById(@Param("id") Long id, @Param("exp") Long exp);
}