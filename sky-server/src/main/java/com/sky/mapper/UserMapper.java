package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from sky_take_out.user where openid = #{openid}")
    User selectByOpenid(String openid);


    void insert(User user);

    @Select("select * from sky_take_out.user where id = #{userId}")
    User getById(Long userId);

    Integer countByMap(Map map);
}
