package com.szm.demo.mapper;

import com.szm.demo.entity.PlayerActionInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PlayerActionInfoMapper {

    @Select("SELECT * FROM player_action_info WHERE battle_id=#{battleId}")
    List<PlayerActionInfo> getByBattleId(@Param("battleId") Long battleId);

    @Select("SELECT * FROM player_action_info WHERE player_id=#{playerId}")
    List<PlayerActionInfo> getByPlayerId(@Param("playerId") Long playerId);

    @Select("SELECT * FROM player_action_info WHERE id=#{id}")
    PlayerActionInfo getById(@Param("id") Long id);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
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
            "(#{item.battleId}, #{item.playerId}, #{item.actionId}, #{item.currentCd}, #{item.restContinueRound},#{item.createTime},#{item.updateTime})",
            "</foreach>",
            "</script>"
    })
    int batchInsert(@Param("playerActionInfoList") List<PlayerActionInfo> playerActionInfoList);

    @Update("UPDATE player_action_info SET current_cd=#{pA.currentCd}, " +
            "rest_continue_round=#{pA.restContinueRound}, update_time=#{pA.updateTime} WHERE " +
            "id=#{pA.id}")
    int updateOne(@Param("pA") PlayerActionInfo playerActionInfo);

    @Update("""
            <script>
            UPDATE player_action_info
            SET
                current_cd = CASE
                    <foreach collection="list" item="pa">
                        WHEN id = #{pa.id} THEN #{pa.currentCd}
                    </foreach>
                    ELSE current_cd
                END,
            
                rest_continue_round = CASE
                    <foreach collection="list" item="pa">
                        WHEN id = #{pa.id} THEN #{pa.restContinueRound}
                    </foreach>
                    ELSE rest_continue_round
                END,
            
                update_time = CURRENT_TIMESTAMP
            
            WHERE id IN
            <foreach collection="list" item="pa" open="(" separator="," close=")">
                #{pa.id}
            </foreach>
            </script>
            """)
    int updateBatch(@Param("list") List<PlayerActionInfo> list);

    @Update("""
            <script>
            UPDATE player_action_info SET battle_id = #{battleId}, update_time = CURRENT_TIMESTAMP
            WHERE id IN
            <foreach collection="list" item="pa" open="(" separator="," close=")">
                #{pa.id}
            </foreach>
            </script>
            """)
    int updateBattleIdBatch(@Param("list") List<PlayerActionInfo> list, @Param("battleId") Long battleId);

    @Delete("DELETE FROM player_action_info WHERE battle_id=#{battleId}")
    int deleteByBattleId(@Param("battleId") Long battleId);
}
