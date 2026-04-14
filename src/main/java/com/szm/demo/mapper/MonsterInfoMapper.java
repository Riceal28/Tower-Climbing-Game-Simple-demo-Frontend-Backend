package com.szm.demo.mapper;

import com.szm.demo.entity.MonsterInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MonsterInfoMapper {

    @Select("SELECT * FROM monster_info WHERE monster_id = #{monsterId}")
    MonsterInfo getByMId(@Param("monsterId") Long monsterId);

    @Select("SELECT * FROM monster_info WHERE id = #{id}")
    MonsterInfo getById(@Param("id") Long id);

    @Select("SELECT  * FROM monster_info WHERE monster_name = #{name}")
    MonsterInfo getByName(@Param("name") String name);
}
