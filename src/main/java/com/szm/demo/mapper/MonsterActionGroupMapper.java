package com.szm.demo.mapper;

import com.szm.demo.entity.MonsterActionGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MonsterActionGroupMapper {

    @Select("SELECT * FROM monster_action_group WHERE monster_id=#{monsterId}")
    List<MonsterActionGroup> getByMId(@Param("monsterId") Long monsterId);
}
