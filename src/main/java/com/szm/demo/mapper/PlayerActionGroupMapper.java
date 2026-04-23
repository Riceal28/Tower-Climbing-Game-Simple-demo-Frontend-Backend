package com.szm.demo.mapper;

import com.szm.demo.entity.PlayerActionGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlayerActionGroupMapper {

    @Select("SELECT * FROM player_action_group WHERE level_id=#{levelId}")
    List<PlayerActionGroup> getByLId(@Param("levelId") Integer levelId);
}
