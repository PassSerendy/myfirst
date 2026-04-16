package com.smartOrder.backend.controller;

import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.dto.UserDTO;
import com.smartOrder.backend.service.UserService;
import com.smartOrder.backend.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器类
 * 处理用户相关的HTTP请求，包括注册、登录、获取用户信息和更新用户信息
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    /**
     * 用户服务接口，用于处理用户相关的业务逻辑
     */
    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     * @param userDTO 用户数据传输对象，包含注册所需的用户信息
     * @return 返回操作结果，包含"注册成功"的提示信息
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDTO){
        userService.register(userDTO);
        return Result.success("注册成功");
    }

    /**
     * 用户登录接口
     * @param userDTO 用户数据传输对象，包含登录所需的用户信息
     * @return 返回操作结果，包含登录成功后生成的token
     */
    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO){
        String token = userService.login(userDTO);
        return Result.success(token);
    }

    /**
     * 获取用户信息接口
     * @return 返回操作结果，包含用户信息的数据传输对象
     */
    @GetMapping("/info")
    public Result<UserVO> info(){
        UserVO userVO = userService.getUserInfo(1L);
        return Result.success(userVO);
    }

    /**
     * 更新用户信息接口
     * @param userVO 用户视图对象，包含需要更新的用户信息
     * @return 返回操作结果，包含"修改成功"的提示信息
     */
    @PutMapping
    public Result<String> update(@RequestBody UserVO userVO){
        userService.updateUserInfo(userVO);
        return Result.success("修改成功");
    }
}
