package com.smartOrder.backend.controller;

import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.UserBehavior;
import com.smartOrder.backend.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/behavior")
public class UserBehaviorController {
    @Autowired
    private UserBehaviorService userBehaviorService;

    // 记录用户行为（前端浏览菜品、加购物车时悄悄调用这个接口）
    @PostMapping
    public Result<String> record(@RequestBody UserBehavior behavior) {
        userBehaviorService.save(behavior);
        return Result.success("记录成功");
    }
}
