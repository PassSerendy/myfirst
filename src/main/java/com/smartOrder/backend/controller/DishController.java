package com.smartOrder.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Dish;
import com.smartOrder.backend.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    // 获取所有菜品列表
    @GetMapping("/list")
    public Result<java.util.List<Dish>> list() {
        java.util.List<Dish> list = dishService.list();
        return Result.success(list);
    }

    // 分页查询菜品（增加了 merchantId 参数，用于区分不同商家的菜品）
    @GetMapping("/page")
    public Result<IPage<Dish>> page(int pageNum, int pageSize, String name, Long merchantId) {
        IPage<Dish> page = dishService.getDishPage(pageNum, pageSize, name, merchantId);
        return Result.success(page);
    }

    // 新增菜品
    @PostMapping("/add")
    public Result<String> add(@RequestBody Dish dish) {
        dishService.save(dish);
        return Result.success("新增成功");
    }

    // 修改菜品
    @PutMapping("/update")
    public Result<String> update(@RequestBody Dish dish) {
        dishService.updateById(dish);
        return Result.success("修改成功");
    }

    // 删除菜品（配合实体类的 @TableLogic，执行的是逻辑删除）
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        dishService.removeById(id);
        return Result.success("删除成功");
    }
}
