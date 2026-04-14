package com.szm.demo.mapper;

import com.szm.demo.entity.SaveInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SaveInfoMapper {

    @Select("SELECT * FROM save_info WHERE id = #{id}")
    SaveInfo getById(@Param("id") Long id);

    @Select("SELECT * FROM save_info WHERE user_id = #{userId}")//todo:修改为分页查询
    List<SaveInfo> getByUserId(@Param("userId") Long userId);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO save_info (user_id, player_id, level, exp, current_hp, current_mp, " +
            "floor, battle_order, progress, is_active) " +
            "VALUES (#{saveInfo.userId},#{saveInfo.playerId},#{saveInfo.level},#{saveInfo.exp}," +
            "#{saveInfo.currentHp},#{saveInfo.currentMp},#{saveInfo.floor}," +
            "#{saveInfo.battleOrder},#{saveInfo.progress},#{saveInfo.isActive})")
    SaveInfo insert(@Param("saveInfo")SaveInfo saveInfo);

    @Update("UPDATE save_info SET level=#{saveInfo.level},exp=#{saveInfo.exp}," +
            "current_hp=#{saveInfo.currentHp},current_mp=#{saveInfo.currentMp},floor=#{saveInfo.floor}," +
            "battle_order=#{saveInfo.battleOrder},progress=#{saveInfo.progress}," +
            "is_active=#{saveInfo.isActivate} " +
            "WHERE id=#{saveInfo.id}")
    int updateSaveById(@Param("saveInfo")SaveInfo saveInfo);

    @Update("UPDATE save_info SET is_active = 0 WHERE user_id = #{userId}")
    int clearActiveByUserId(@Param("userId")Long userId);

    @Select("SELECT * FROM save_info WHERE user_id = #{userId} AND is_active = 1")
    SaveInfo getActiveSave(@Param("userId")Long userId);
}
