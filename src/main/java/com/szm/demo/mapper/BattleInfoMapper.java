package com.szm.demo.mapper;

import com.szm.demo.entity.BattleInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface BattleInfoMapper {

    @Select("SELECT * FROM battle_info WHERE save_id = #{saveId}")
    BattleInfo getBySaveId(@Param("saveId")Long saveId);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO battle_info (save_id, user_id, monster_id, user_current_hp, user_current_mp, " +
            "user_current_defend, monster_current_hp, monster_current_mp, monster_current_defend, " +
            "create_time, update_time) VALUES (#{battleInfo.saveId},#{battleInfo.userId}," +
            "#{battleInfo.monsterId},#{battleInfo.userCurrentHp},#{battleInfo.userCurrentMp}," +
            "#{battleInfo.userCurrentDefend},#{battleInfo.monsterCurrentHp}," +
            "#{battleInfo.monsterCurrentMp},#{battleInfo.monsterCurrentDefend},#{battleInfo.createTime}" +
            "#{battleInfo.updateTime})")
    BattleInfo insert(@Param("battleInfo")BattleInfo battleInfo);
}
