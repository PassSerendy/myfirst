package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.UserBehavior;
import com.smartOrder.backend.service.UserBehaviorService;
import com.smartOrder.backend.mapper.UserBehaviorMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_behavior(用户行为表(用于AI推荐计算))】的数据库操作Service实现
* @createDate 2026-04-02 22:41:34
*/
@Service
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior>
    implements UserBehaviorService{

}




