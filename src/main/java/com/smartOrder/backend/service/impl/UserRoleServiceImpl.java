package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.UserRole;
import com.smartOrder.backend.service.UserRoleService;
import com.smartOrder.backend.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_role(用户-角色关联表)】的数据库操作Service实现
* @createDate 2026-04-02 22:41:36
*/
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService{

}




