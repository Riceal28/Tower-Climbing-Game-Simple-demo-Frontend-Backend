package com.szm.demo.mapper;

import com.szm.demo.entity.UserDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDetailMapper {

    /**
     * 根据ID查询用户详情
     */
    @Select("SELECT * FROM user_detail WHERE id = #{id}")
    UserDetail getById(@Param("id") long id);

    /**
     * 根据userId查询用户详情（最常用）
     */
    @Select("SELECT * FROM user_detail WHERE user_id = #{userId}")
    UserDetail getByUserId(@Param("userId") Long userId);

    /**
     * 分页查询所有用户详情
     */
    @Select("SELECT * FROM user_detail LIMIT #{offset}, #{maxResults}")
    List<UserDetail> getAll(@Param("offset") int offset, @Param("maxResults") int maxResults);

    /**
     * 新增用户详情
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO user_detail (user_id, level, exp, attack_base, current_hp, current_mp) " +
            "VALUES (#{userDetail.userId},#{userDetail.level},#{userDetail.exp},#{userDetail.attackBase},#{userDetail.currentHp},#{userDetail.currentMp})")
    void insert(@Param("userDetail") UserDetail userDetail);

    /**
     * 根据ID更新等级、经验、基础攻击
     */
    @Update("UPDATE user_detail SET level=#{userDetail.level}, exp=#{userDetail.exp}, attack_base=#{userDetail.attackBase} " +
            "WHERE id=#{userDetail.id}")
    int updateLevelExpById(@Param("userDetail") UserDetail userDetail);

    /**
     * 根据ID更新血量、蓝量
     */
    @Update("UPDATE user_detail SET current_hp=#{userDetail.currentHp}, current_mp=#{userDetail.currentMp} " +
            "WHERE id=#{userDetail.id}")
    int updateHpMpById(@Param("userDetail") UserDetail userDetail);

    /**
     * 根据userId更新血量、蓝量
     */
    @Update("UPDATE user_detail SET current_hp=#{userDetail.currentHp}, current_mp=#{userDetail.currentMp} " +
            "WHERE user_id=#{userDetail.userId}")
    int updateHpMpByUserId(@Param("userDetail") UserDetail userDetail);
    /**
     * 根据userId更新血量、蓝量
     */
    @Update("UPDATE user_detail SET level=#{userDetail.level}, exp=#{userDetail.exp}, attack_base=#{userDetail.attackBase}, current_hp=#{userDetail.currentHp}, current_mp=#{userDetail.currentMp} " +
            "WHERE user_id=#{userDetail.userId}")
    int updateAllByUserId(@Param("userDetail") UserDetail userDetail);
    /**
     * 根据userId更新经验
     */
    @Update("UPDATE user_detail SET exp=#{exp} WHERE user_id=#{userId}")
    int updateExpByUserId(@Param("userId") Long userId,@Param("exp") Long exp);
}