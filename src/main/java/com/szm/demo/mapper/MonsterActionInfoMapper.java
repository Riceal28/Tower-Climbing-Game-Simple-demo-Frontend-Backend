package com.szm.demo.mapper;

import com.szm.demo.entity.MonsterActionInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper//todo:与角色技能合并
public interface MonsterActionInfoMapper {

    @Select("SELECT * FROM monster_action_info WHERE battle_id=#{battleId}")
    List<MonsterActionInfo> getByBattleId(@Param("battleId") Long battleId);

    @Select("SELECT * FROM monster_action_info WHERE id=#{id}")
    MonsterActionInfo getById(@Param("id")Long id);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")//todo:时间
    @Insert("INSERT INTO monster_action_info (battle_id, monster_id, action_id, current_cd, " +
            "rest_continue_round, create_time, update_time) " +
            "VALUES (#{monsterActionInfo.battleId},#{monsterActionInfo.playerId},#{monsterActionInfo.actionId}," +
            "#{monsterActionInfo.currentCd},#{monsterActionInfo.restContinueRound}," +
            "#{monsterActionInfo.createTime},#{monsterActionInfo.updateTime})")
    void insert(@Param("monsterActionInfo") MonsterActionInfo monsterActionInfo);

    @Insert({
            "<script>",
            "INSERT INTO monster_action_info (battle_id, monster_id, action_id, current_cd, rest_continue_round," +
                    "create_time,update_time) ",
            "VALUES ",
            "<foreach collection='monsterActionInfoList' item='item' separator=','>",
            "(#{item.battleId}, #{item.userId}, #{item.actionId}, #{item.currentCd}, #{item.restContinueRound},#{item.createTime},#{item.updateTime})",
            "</foreach>",
            "</script>"
    })
    int batchInsert(@Param("monsterActionInfoList") List<MonsterActionInfo> monsterActionInfoList);

    @Update("UPDATE monster_action_info SET current_cd=#{mA.currentCd}, " +
            "rest_continue_round=#{mA.restContinueRound}, update_time=#{mA.updateTime} WHERE " +
            "id=#{mA.id}")
    int updateOne(@Param("mA") MonsterActionInfo monsterActionInfo);

    @Update("""
            <script>
            UPDATE monster_action_info
            <set>
                <foreach collection="list" item="pa" separator="">
                    current_cd =
                    CASE id
                        WHEN #{mA.id} THEN #{mA.currentCd}
                    END,
                </foreach>
            
                <foreach collection="list" item="mA" separator="">
                    rest_continue_round =
                    CASE id
                        WHEN #{mA.id} THEN #{mA.restContinueRound}
                    END,
                </foreach>
            
                update_time = CURRENT_TIMESTAMP
            </set>
            WHERE id IN
            <foreach collection="list" item="mA" open="(" separator="," close=")">
                #{mA.id}
            </foreach>
            </script>
            """)
    int updateBatch(@Param("list") List<MonsterActionInfo> list);
}
