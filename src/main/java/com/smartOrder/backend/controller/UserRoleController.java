package com.smartOrder.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.UserRole;
import com.smartOrder.backend.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userRole")
public class UserRoleController {
    @Autowired
    private UserRoleService userRoleService;

    // 根据用户ID查询他拥有什么角色
    @GetMapping("/user/{userId}")
    public Result<?> getByUserId(@PathVariable Long userId) {
        return Result.success(userRoleService.list(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)));
    }

    // 给用户绑定角色
    @PostMapping
    public Result<String> bind(@RequestBody UserRole userRole) {
        userRoleService.save(userRole);
        return Result.success("绑定角色成功");
    }

    // 解绑角色
    @DeleteMapping
    public Result<String> unbind(@RequestParam Long userId, @RequestParam Long roleId) {
        userRoleService.remove(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleId, roleId));
        return Result.success("解绑角色成功");
    }
}
