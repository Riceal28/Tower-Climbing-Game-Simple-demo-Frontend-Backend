package com.szm.demo.mapper;

import com.szm.demo.entity.SaveInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SaveInfoMapper {

    @Select("SELECT * FROM save_info WHERE id = #{id}")
    SaveInfo getById(@Param("id") Long id);

    @Select("SELECT * FROM save_info WHERE user_id = #{userId}")
    List<SaveInfo> getAllByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM save_info WHERE player_id = #{player}")
    List<SaveInfo> getByPlayerId(@Param("playerId") Long playerId);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO save_info (user_id, player_id, level, exp, current_hp, current_mp, " +
            "floor, battle_order, progress) " +
            "VALUES (#{saveInfo.userId},#{saveInfo.playerId},#{saveInfo.level},#{saveInfo.exp}," +
            "#{saveInfo.currentHp},#{saveInfo.currentMp},#{saveInfo.floor}," +
            "#{saveInfo.battleOrder},#{saveInfo.progress})")
    SaveInfo insert(@Param("saveInfo")SaveInfo saveInfo);

    @Update("UPDATE save_info SET level=#{saveInfo.level},exp=#{saveInfo.exp}," +
            "current_hp=#{saveInfo.currentHp},current_mp=#{saveInfo.currentMp},floor=#{saveInfo.floor}," +
            "battle_order=#{saveInfo.battleOrder},progress=#{saveInfo.progress} " +
            "WHERE id=#{saveInfo.id}")
    int updateSaveById(@Param("saveInfo")SaveInfo saveInfo);
}
