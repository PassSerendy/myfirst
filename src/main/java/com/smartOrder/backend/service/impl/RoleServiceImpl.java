package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Role;
import com.smartOrder.backend.service.RoleService;
import com.smartOrder.backend.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【role】的数据库操作Service实现
* @createDate 2026-04-02 22:41:29
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

}




