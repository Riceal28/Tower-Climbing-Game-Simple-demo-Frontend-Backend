package com.szm.demo.mapper;

import com.szm.demo.common.PlayerClass;
import com.szm.demo.entity.LevelInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LevelInfoMapper {

    /**
     * 根据ID查询等级配置
     */
    @Select("SELECT * FROM level_info WHERE id = #{id}")
    LevelInfo getById(@Param("id") Integer id);

    /**
     * 根据职阶与等级查询等级配置
     */
    @Select("SELECT * FROM level_info WHERE player_class=#{playerClass} AND level = #{level}")
    LevelInfo getByClassLevel(@Param("playerClass")PlayerClass playerClass, @Param("level") Integer level);

    /**
     * 分页查询所有等级配置
     */
    @Select("SELECT * FROM level_info LIMIT #{offset}, #{maxResults}")
    List<LevelInfo> getAll(@Param("offset") int offset, @Param("maxResults") int maxResults);

    /**
     * 新增等级配置
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO level_info (player_class, level, needed_exp, max_hp, max_mp, attack_base) " +
            "VALUES (#{levelInfo.playerClass}, #{levelInfo.level}, #{levelInfo.neededExp}, #{levelInfo.maxHp}, #{levelInfo.maxMp}, #{levelInfo.attackBase})")
    void insert(@Param("levelInfo") LevelInfo levelInfo);

    /**
     * 根据ID更新等级配置
     */
    @Update("UPDATE level_info SET level=#{levelInfo.level}, needed_exp=#{levelInfo.neededExp}, " +
            "max_hp=#{levelInfo.maxHp}, max_mp=#{levelInfo.maxMp}, attack_base=#{levelInfo.attackBase} " +
            "WHERE id=#{levelInfo.id}")
    int updateById(@Param("levelInfo") LevelInfo levelInfo);

    /**
     * 根据ID删除等级配置
     */
    @Delete("DELETE FROM level_info WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}