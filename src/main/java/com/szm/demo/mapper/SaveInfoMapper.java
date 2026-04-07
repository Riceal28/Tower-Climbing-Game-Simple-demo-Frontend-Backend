package com.szm.demo.mapper;

import com.szm.demo.entity.SaveInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SaveInfoMapper {

    @Select("SELECT * FROM save_info WHERE id = #{id}")
    SaveInfo getById(@Param("id") Long id);

    @Select("SELECT * FROM save_info WHERE user_id = #{userId}")
    List<SaveInfo> getByUserId(@Param("userId") Long userId);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO save_info (user_id, level, exp, floor, progress, monster_id, is_active) " +
            "VALUES (#{saveInfo.userId},#{saveInfo.level},#{saveInfo.exp},#{saveInfo.floor},#{saveInfo.progress},#{saveInfo.monsterId},#{saveInfo.isActive})")
    SaveInfo insert(@Param("saveInfo")SaveInfo saveInfo);

    @Update("UPDATE save_info SET level=#{saveInfo.level},exp=#{saveInfo.exp},floor=#{saveInfo.floor}," +
            "progress=#{saveInfo.progress},monster_id=#{saveInfo.monsterId},is_active=#{saveInfo.isActivate} " +
            "WHERE id=#{saveInfo.id}")
    int updateSaveById(@Param("saveInfo")SaveInfo saveInfo);

    @Update("UPDATE save_info")
    int clearActiveByUserId(@Param("userId")Long userId);
}
