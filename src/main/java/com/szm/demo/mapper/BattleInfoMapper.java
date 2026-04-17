package com.szm.demo.mapper;

import com.szm.demo.entity.BattleInfo;
import com.szm.demo.entity.SaveInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BattleInfoMapper {

    @Select("SELECT * FROM battle_info WHERE save_id = #{saveId}")
    BattleInfo getBySaveId(@Param("saveId")Long saveId);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO battle_info (save_id, monster_id, player_current_hp, player_current_mp, " +
            "player_current_defend, monster_current_hp, monster_current_mp, monster_current_defend, " +
            "create_time, update_time) VALUES (#{battleInfo.saveId},#{battleInfo.monsterId}," +
            "#{battleInfo.playerCurrentHp},#{battleInfo.playerCurrentMp}," +
            "#{battleInfo.playerCurrentDefend},#{battleInfo.monsterCurrentHp}," +
            "#{battleInfo.monsterCurrentMp},#{battleInfo.monsterCurrentDefend},#{battleInfo.createTime}" +
            "#{battleInfo.updateTime})")
    BattleInfo insert(@Param("battleInfo")BattleInfo battleInfo);

    @Update("UPDATE battle_info SET (monster_id, player_current_hp, player_current_mp, " +
            "player_current_defend, monster_current_hp, monster_current_mp, " +
            "monster_current_defend, update_time) VALUE (#{battleInfo.monsterId}," +
            "#{battleInfo.playerCurrentHp},#{battleInfo.playerCurrentMp},#{battleInfo.playerCurrentDefend}" +
            "#{battleInfo.monsterCurrentHp},#{battleInfo.monsterCurrentMp},#{battleInfo.monsterCurrentDefend}" +
            "#{battleInfo.updateTime}) WHERE id = #{battleInfo.id}")
    void updateById(@Param("battleInfo")BattleInfo battleInfo);

    @Update("UPDATE battle_info SET (monster_id, player_current_hp, player_current_mp, " +
            "player_current_defend, monster_current_hp, monster_current_mp, " +
            "monster_current_defend, update_time) VALUE (#{battleInfo.monsterId}," +
            "#{battleInfo.playerCurrentHp},#{battleInfo.playerCurrentMp},#{battleInfo.playerCurrentDefend}" +
            "#{battleInfo.monsterCurrentHp},#{battleInfo.monsterCurrentMp},#{battleInfo.monsterCurrentDefend}" +
            "#{battleInfo.updateTime}) WHERE save_id = #{battleInfo.saveId}")
    void updateBySaveId(@Param("saveInfo") BattleInfo battleInfo);
}
