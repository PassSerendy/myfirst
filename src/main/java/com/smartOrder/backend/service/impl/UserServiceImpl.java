package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.common.util.JwtUtil;
import com.smartOrder.backend.common.util.PasswordUtil;
import com.smartOrder.backend.dto.UserDTO;
import com.smartOrder.backend.entity.User;
import com.smartOrder.backend.exception.CustomException;
import com.smartOrder.backend.service.UserService;
import com.smartOrder.backend.mapper.UserMapper;
import com.smartOrder.backend.vo.UserVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2026-04-02 22:38:21
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper; // 注入UserMapper，用于数据库操作
    @Resource
    private JwtUtil jwtUtil; // 注入JwtUtil，用于生成和验证JWT token
    /**
     * 用户注册方法
     * @param userDTO 用户注册数据传输对象
     * @throws CustomException 当用户名或手机号已存在时抛出异常
     */
    @Override
    public void register(UserDTO userDTO){
        //检查用户名或手机号是否已存在
        if (userMapper.selectByUsername(userDTO.getUsername())!=null){
            throw new CustomException("用户名已存在");
        }
        if (userMapper.selectByPhone(userDTO.getPhone())!=null){
            throw new CustomException("手机号已存在");
        }

        //加密密码
        String encryptedPassword = PasswordUtil.encrypt(userDTO.getPassword());

        //插入数据库
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(encryptedPassword); // 设置加密后的密码
        userMapper.insert(user);
    }

    /**
     * 用户登录方法
     * @param userDTO 用户登录数据传输对象
     * @return JWT token字符串
     * @throws CustomException 当用户名或密码错误时抛出异常
     */
    @Override
    public String login(UserDTO userDTO){
        //查用户
        User user = userMapper.selectByUsername(userDTO.getUsername());
        if(user == null){
            throw new CustomException("用户名或密码错误");
        }
        //验证密码
        if(!PasswordUtil.check(userDTO.getPassword(),user.getPassword())){
            throw new CustomException("用户名或密码错误");
        }

        //生成 JWT token
        return jwtUtil.generateToken(user.getId(),user.getUsername());
    }

    /**
     * 获取用户信息方法
     * @param userId 用户ID
     * @return UserVO 用户视图对象
     * @throws CustomException 当用户不存在时抛出异常
     */
    @Override
    public UserVO getUserInfo(Long userId){
        User user = userMapper.selectById(userId);
        if(user == null){
            throw new CustomException("用户不存在");
        }
        UserVO vo =new UserVO();
        BeanUtils.copyProperties(user,vo); // 属性拷贝
        return vo;
    }

    /**
     * 更新用户信息方法
     * @param userVO 用户视图对象，包含要更新的用户信息
     */
    @Override
    public void updateUserInfo(UserVO userVO){
        User user = new User();
        BeanUtils.copyProperties(userVO,user); // 属性拷贝
        userMapper.updateById(user);
    }
}




