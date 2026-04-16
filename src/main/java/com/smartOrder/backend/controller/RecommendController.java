package com.smartOrder.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Dish;
import com.smartOrder.backend.entity.UserBehavior;
import com.smartOrder.backend.service.DishService;
import com.smartOrder.backend.service.RecommendRecordService;
import com.smartOrder.backend.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    private UserBehaviorService behaviorService;
    @Autowired
    private DishService dishService;
    @Autowired
    private RecommendRecordService recommendRecordService;

    /**
     * 获取个性化推荐（基于用户行为加权计算）
     */
    @GetMapping("/{userId}")
    public Result<List<Dish>> getPersonalRecommend(@PathVariable Long userId) {
        // 1. 查出该用户所有的行为记录
        List<UserBehavior> behaviors = behaviorService.list(new LambdaQueryWrapper<UserBehavior>()
                .eq(UserBehavior::getUserId, userId));

        // 2. 统计每个菜品的“热度权重分”（购买*4 + 加购*3 + 收藏*2 + 点击*1）
        Map<Long, Integer> dishScoreMap = new HashMap<>();
        for (UserBehavior b : behaviors) {
            Integer score = dishScoreMap.getOrDefault(b.getDishId(), 0);
            switch (b.getAction()) {
                case 4: score += 4; break; // 购买
                case 3: score += 3; break; // 加购
                case 2: score += 2; break; // 收藏
                case 1: score += 1; break; // 点击
            }
            dishScoreMap.put(b.getDishId(), score);
        }

        // 3. 按分数从高到低排序，取前 5 名
        List<Long> topDishIds = dishScoreMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (topDishIds.isEmpty()) {
            return Result.success(new ArrayList<>()); // 新用户没行为，返回空
        }

        // 4. 根据排好序的ID去查菜品详情
        List<Dish> recommendDishes = topDishIds.stream()
                .map(dishService::getById)
                .filter(dish -> dish != null && dish.getStatus() == 1) // 过滤掉停售的
                .collect(Collectors.toList());

        // 5. (可选) 将推荐结果存入 recommend_record 表留痕，type=2代表AI推荐
        // 此处省略存库代码，避免卡顿，直接返回

        return Result.success(recommendDishes);
    }

    /**
     * 获取热门推荐（全站所有人都在买的，不需要区分用户）
     */
    @GetMapping("/hot")
    public Result<List<Dish>> getHotRecommend() {
        // 查出所有人“购买(action=4)”的行为记录
        List<UserBehavior> buyBehaviors = behaviorService.list(new LambdaQueryWrapper<UserBehavior>()
                .eq(UserBehavior::getAction, 4));

        // 统计哪个菜被买得最多
        Map<Long, Integer> buyCountMap = new HashMap<>();
        for (UserBehavior b : buyBehaviors) {
            buyCountMap.put(b.getDishId(), buyCountMap.getOrDefault(b.getDishId(), 0) + 1);
        }

        List<Long> hotDishIds = buyCountMap.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<Dish> hotDishes = hotDishIds.stream()
                .map(dishService::getById)
                .filter(dish -> dish != null && dish.getStatus() == 1)
                .collect(Collectors.toList());

        return Result.success(hotDishes);
    }
}
