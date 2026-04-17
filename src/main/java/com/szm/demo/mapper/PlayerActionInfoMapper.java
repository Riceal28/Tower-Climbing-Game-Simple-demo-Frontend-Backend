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

    @Update("UPDATE player_action_info SET current_cd=#{pA.currentCd}, " +
            "rest_continue_round=#{pA.restContinueRound}, update_time=#{pA.updateTime} WHERE " +
            "id=#{pA.id}")
    int updateOne(@Param("pA") PlayerActionInfo playerActionInfo);

    @Update("""
            <script>
            UPDATE player_action_info
            <set>
                <foreach collection="list" item="pa" separator="">
                    current_cd =
                    CASE id
                        WHEN #{pa.id} THEN #{pa.currentCd}
                    END,
                </foreach>
            
                <foreach collection="list" item="pa" separator="">
                    rest_continue_round =
                    CASE id
                        WHEN #{pa.id} THEN #{pa.restContinueRound}
                    END,
                </foreach>
            
                update_time = CURRENT_TIMESTAMP
            </set>
            WHERE id IN
            <foreach collection="list" item="pa" open="(" separator="," close=")">
                #{pa.id}
            </foreach>
            </script>
            """)
    int updateBatch(@Param("list") List<PlayerActionInfo> list);
}
