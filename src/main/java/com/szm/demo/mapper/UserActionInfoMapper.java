package com.szm.demo.mapper;

import com.szm.demo.entity.UserActionInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserActionInfoMapper {

    @Select("SELECT * FROM user_action_info WHERE user_id=#{userId}")
    List<UserActionInfo> getByUserId(@Param("userId")Long userId);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO user_action_info (battle_id, user_id, action_id, current_cd, rest_continue_round) " +
            "VALUES (#{userActionInfo.battleId},#{userActionInfo.userId},#{userActionInfo.actionId}," +
            "#{userActionInfo.currentCd},#{userActionInfo.restContinueRound})")
    void insert(@Param("userActionInfo")UserActionInfo userActionInfo);

    @Insert({
            "<script>",
            "INSERT INTO user_action_info (battle_id, user_id, action_id, current_cd, rest_continue_round) ",
            "VALUES ",
            "<foreach collection='userActionInfoList' item='item' separator=','>",
            "(#{item.battleId}, #{item.userId}, #{item.actionId}, #{item.currentCd}, #{item.restContinueRound})",
            "</foreach>",
            "</script>"
    })
    int batchInsert(@Param("userActionInfoList") List<UserActionInfo> userActionInfoList);
}
