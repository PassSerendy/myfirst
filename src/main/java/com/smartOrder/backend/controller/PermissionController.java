package com.smartOrder.backend.controller;

import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Permission;
import com.smartOrder.backend.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(permissionService.list());
    }

    @PostMapping
    public Result<String> save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.success("添加权限成功");
    }
}
