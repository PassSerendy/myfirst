package com.smartOrder.backend.mapper;

import com.smartOrder.backend.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2026-04-02 22:38:21
* @Entity com.smartOrder.backend.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(String username);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User selectByPhone(String phone);
}




