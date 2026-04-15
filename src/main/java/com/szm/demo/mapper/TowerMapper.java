package com.szm.demo.mapper;

import com.szm.demo.entity.TowerFloorInfo;
import com.szm.demo.entity.TowerFloorMonsterInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TowerMapper {

    @Select("SELECT * FROM tower_floor_info WHERE floor = #{floor}")
    TowerFloorInfo getBaseByFloor(@Param("floor") Integer floor);

    @Select("SELECT * FROM tower_floor_info WHERE id = #{id}")
    TowerFloorInfo getBaseById(@Param("id") Long id);

    @Select("SELECT * FROM tower_floor_monster_info WHERE floor = #{floor}")
    List<TowerFloorMonsterInfo> getAllDetailByFloor(@Param("floor") Integer floor);

    @Select("SELECT * FROM tower_floor_monster_info WHERE floor = #{floor} " +
            "AND battle_order = #{battleOrder}")
    TowerFloorMonsterInfo getOneDetailByOrder(@Param("floor") Integer floor,
                                              @Param("battleOrder") Integer battleOrder);

    @Select("SELECT * FROM tower_floor_monster_info WHERE id = #{id}")
    TowerFloorMonsterInfo getOneDetailById(@Param("id") Long id);
}
