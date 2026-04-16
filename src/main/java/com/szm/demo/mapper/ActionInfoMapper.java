package com.szm.demo.mapper;

import com.szm.demo.entity.ActionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ActionInfoMapper {

    @Select("SELECT * FROM action_info WHERE action_id = #{actionId}")
    ActionInfo getByAId(@Param("actionId")Long actionId);
}
