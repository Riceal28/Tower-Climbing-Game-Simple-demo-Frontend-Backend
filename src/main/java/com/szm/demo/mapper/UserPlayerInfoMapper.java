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

//    /**
//     * 根据userId查询用户详情
//     */
//    @Select("SELECT * FROM user_player_info WHERE user_id = #{userId}")
//    UserPlayerInfo getByUserId(@Param("userId") Long userId);

//    /**
//     * 分页查询所有角色信息
//     */
//    @Select("SELECT * FROM user_player_info LIMIT #{offset}, #{maxResults}")
//    List<UserPlayerInfo> getAll(@Param("offset") int offset, @Param("maxResults") int maxResults);

    /**
     * 根据用户ID分页查询用户下所有角色信息
     */
    @Select("SELECT * FROM user_player_info WHERE user_id =#{userId} ORDER BY id ASC " +
            "LIMIT #{offset}, #{maxResults}")
    List<UserPlayerInfo> pageByUserId(@Param("userId") Long userId,
                                      @Param("offset") int offset,
                                      @Param("maxResults") int maxResults);

    /**
     * 新增用户角色
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO user_player_info (user_id, player_class, level, exp) " +
            "VALUES (#{userPlayerInfo.userId},#{userPlayerInfo.playerClass},#{userPlayerInfo.level},#{userPlayerInfo.exp})")
    void insert(@Param("userPlayerInfo") UserPlayerInfo userPlayerInfo);

//    /**
//     * 根据ID更新等级、经验、基础攻击
//     */
//    @Update("UPDATE user_player_info SET level=#{userDetail.level}, exp=#{userDetail.exp}, attack_base=#{userDetail.attackBase} " +
//            "WHERE id=#{userDetail.id}")
//    int updateLevelExpById(@Param("userDetail") UserDetail userDetail);

//    /**
//     * 根据ID更新血量、蓝量
//     */
//    @Update("UPDATE user_player_info SET current_hp=#{userDetail.currentHp}, current_mp=#{userDetail.currentMp} " +
//            "WHERE id=#{userDetail.id}")
//    int updateHpMpById(@Param("userDetail") UserDetail userDetail);

//    /**
//     * 根据userId更新血量、蓝量
//     */
//    @Update("UPDATE user_player_info SET current_hp=#{userDetail.currentHp}, current_mp=#{userDetail.currentMp} " +
//            "WHERE user_id=#{userDetail.userId}")
//    int updateHpMpByUserId(@Param("userDetail") UserDetail userDetail);

    /**
     * 根据ID更新全部信息
     */
    @Update("UPDATE user_player_info SET level=#{userPlayerInfo.level}, exp=#{userPlayerInfo.exp} " +
            "WHERE user_id=#{userPlayerInfo.userId}")
    int updateAllById(@Param("userPlayerInfo") UserPlayerInfo userPlayerInfo);

    /**
     * 根据ID更新经验
     */
    @Update("UPDATE user_player_info SET exp=#{exp} WHERE id=#{id}")
    int updateExpById(@Param("id") Long id, @Param("exp") Long exp);
}