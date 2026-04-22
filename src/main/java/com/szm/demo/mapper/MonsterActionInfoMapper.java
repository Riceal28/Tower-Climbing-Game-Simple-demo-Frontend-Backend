package com.szm.demo.mapper;

import com.szm.demo.entity.MonsterActionInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MonsterActionInfoMapper {

    @Select("SELECT * FROM monster_action_info WHERE battle_id=#{battleId}")
    List<MonsterActionInfo> getByBattleId(@Param("battleId") Long battleId);

    @Select("SELECT * FROM monster_action_info WHERE monster_id=#{monsterId}")
    List<MonsterActionInfo> getByMonsterId(@Param("monsterId") Long monsterId);

    @Select("SELECT * FROM monster_action_info WHERE id=#{id}")
    MonsterActionInfo getById(@Param("id")Long id);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO monster_action_info (battle_id, monster_id, action_id, current_cd, " +
            "rest_continue_round, create_time, update_time) " +
            "VALUES (#{monsterActionInfo.battleId},#{monsterActionInfo.monsterId},#{monsterActionInfo.actionId}," +
            "#{monsterActionInfo.currentCd},#{monsterActionInfo.restContinueRound}," +
            "#{monsterActionInfo.createTime},#{monsterActionInfo.updateTime})")
    void insert(@Param("monsterActionInfo") MonsterActionInfo monsterActionInfo);

    @Insert({
            "<script>",
            "INSERT INTO monster_action_info (battle_id, monster_id, action_id, current_cd, rest_continue_round," +
                    "create_time,update_time) ",
            "VALUES ",
            "<foreach collection='monsterActionInfoList' item='item' separator=','>",
            "(#{item.battleId}, #{item.monsterId}, #{item.actionId}, #{item.currentCd}, #{item.restContinueRound},#{item.createTime},#{item.updateTime})",
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
                <foreach collection="list" item="ma" separator="">
                    current_cd =
                    CASE id
                        WHEN #{ma.id} THEN #{ma.currentCd}
                    END,
                </foreach>
            
                <foreach collection="list" item="ma" separator="">
                    rest_continue_round =
                    CASE id
                        WHEN #{ma.id} THEN #{ma.restContinueRound}
                    END,
                </foreach>
            
                update_time = CURRENT_TIMESTAMP
            </set>
            WHERE id IN
            <foreach collection="list" item="ma" open="(" separator="," close=")">
                #{ma.id}
            </foreach>
            </script>
            """)
    int updateBatch(@Param("list") List<MonsterActionInfo> list);

    @Update("""
            <script>
            UPDATE monster_action_info SET battle_id = #{battleId}, update_time = CURRENT_TIMESTAMP
            WHERE id IN
            <foreach collection="list" item="ma" open="(" separator="," close=")">
                #{ma.id}
            </foreach>
            </script>
            """)
    int updateBattleIdBatch(@Param("list") List<MonsterActionInfo> list,@Param("battleId")Long battleId);

    @Delete("DELETE FROM monster_action_info WHERE battle_id=#{battleId}")
    int deleteByBattleId(@Param("battleId") Long battleId);
}
