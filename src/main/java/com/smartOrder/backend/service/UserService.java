package com.smartOrder.backend.service;

import com.smartOrder.backend.dto.UserDTO;
import com.smartOrder.backend.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smartOrder.backend.vo.UserVO;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2026-04-02 22:38:21
*/
public interface UserService extends IService<User> {
    //注册
    void register(UserDTO userDTO);
    //登录
    String login(UserDTO userDTO);
    //获取用户信息
    UserVO getUserInfo(Long userId);
    //修改用户信息
    void updateUserInfo(UserVO userVO);

}
