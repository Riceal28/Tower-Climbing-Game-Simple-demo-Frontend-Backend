package com.szm.demo.mapper;

import com.szm.demo.entity.UserInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserInfoMapper {

    @Select("SELECT * FROM user_info WHERE id = #{id}")
    UserInfo getById(@Param("id") long id);

    @Select("SELECT * FROM user_info WHERE email = #{email}")
    UserInfo getByEmail(@Param("email") String email);

    @Select("SELECT * FROM user_info WHERE username = #{username}")
    UserInfo getByUsername(@Param("username") String username);

    @Select("SELECT * FROM user_info LIMIT #{offset}, #{maxResults}")
    List<UserInfo> getAll(@Param("offset") int offset, @Param("maxResults") int maxResults);

    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    @Insert("INSERT INTO user_info (email, username, password) VALUES (#{userInfo.email},#{userInfo.username},#{userInfo.password})")
    void insert(@Param("userInfo") UserInfo userInfo);

    @Update("UPDATE user_info SET email=#{userInfo.email} WHERE id=#{userInfo.id}")
    int updateEmailById(@Param("userInfo") UserInfo userInfo);

    @Update("UPDATE user_info SET password=#{userInfo.password} WHERE id=#{userInfo.id}")
    int updatePassById(@Param("userInfo") UserInfo userInfo);
}
