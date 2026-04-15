package com.szm.demo.mapper;

import com.szm.demo.entity.PlayerActionInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlayerActionInfoMapper {

    @Select("SELECT * FROM player_action_info WHERE battle_id=#{battleId}")
    List<PlayerActionInfo> getByBattleId(@Param("battleId") Long battleId);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")//todo:时间
    @Insert("INSERT INTO player_action_info (battle_id, player_id, action_id, current_cd, " +
            "rest_continue_round, create_time, update_time) " +
            "VALUES (#{playerActionInfo.battleId},#{playerActionInfo.playerId},#{playerActionInfo.actionId}," +
            "#{playerActionInfo.currentCd},#{playerActionInfo.restContinueRound}," +
            "#{playerActionInfo.createTime},#{playerActionInfo.updateTime})")
    void insert(@Param("playerActionInfo") PlayerActionInfo playerActionInfo);

    @Insert({
            "<script>",
            "INSERT INTO player_action_info (battle_id, player_id, action_id, current_cd, rest_continue_round," +
                    "create_time,update_time) ",
            "VALUES ",
            "<foreach collection='playerActionInfoList' item='item' separator=','>",
            "(#{item.battleId}, #{item.userId}, #{item.actionId}, #{item.currentCd}, #{item.restContinueRound},#{item.createTime},#{item.updateTime})",
            "</foreach>",
            "</script>"
    })
    int batchInsert(@Param("playerActionInfoList") List<PlayerActionInfo> playerActionInfoList);
}
