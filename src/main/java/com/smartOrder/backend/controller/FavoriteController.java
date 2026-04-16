package com.smartOrder.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Dish;
import com.smartOrder.backend.entity.Favorite;
import com.smartOrder.backend.service.DishService;
import com.smartOrder.backend.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private DishService dishService;

    // 收藏/取消收藏 (合二为一的优雅写法)
    @PostMapping("/{dishId}")
    public Result<String> toggleFavorite(@PathVariable Long dishId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        // 查询是否已经收藏过
        Favorite fav = favoriteService.getOne(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getDishId, dishId));

        if (fav != null) {
            // 已收藏，则取消收藏
            favoriteService.removeById(fav.getId());
            return Result.success("已取消收藏");
        } else {
            // 未收藏，则添加收藏
            Favorite newFav = new Favorite();
            newFav.setUserId(userId);
            newFav.setDishId(dishId);
            favoriteService.save(newFav);
            return Result.success("收藏成功");
        }
    }

    // 获取我的收藏列表（不仅要返回收藏记录，还要把菜品的具体信息带上）
    @GetMapping("/list")
    public Result<List<Dish>> myList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        // 1. 查出我收藏的所有 dishId
        List<Favorite> list = favoriteService.list(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .orderByDesc(Favorite::getCreateTime));

        // 2. 根据 dishId 批量查出菜品信息返回给前端
        List<Dish> dishList = list.stream()
                .map(fav -> dishService.getById(fav.getDishId()))
                .filter(dish -> dish != null && dish.getStatus() == 1) // 过滤掉被删的或停售的
                .collect(Collectors.toList());

        return Result.success(dishList);
    }
}
