package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Permission;
import com.smartOrder.backend.service.PermissionService;
import com.smartOrder.backend.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【permission(权限表)】的数据库操作Service实现
* @createDate 2026-04-02 22:41:23
*/
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>
    implements PermissionService{

}




