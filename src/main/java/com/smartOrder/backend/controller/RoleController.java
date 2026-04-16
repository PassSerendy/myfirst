package com.smartOrder.backend.controller;

import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Role;
import com.smartOrder.backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.success(roleService.list());
    }

    @PostMapping
    public Result<String> save(@RequestBody Role role) {
        roleService.save(role);
        return Result.success("添加角色成功");
    }
}
