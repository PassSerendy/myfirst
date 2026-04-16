package com.smartOrder.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Category;
import com.smartOrder.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 新增分类
    @PostMapping
    public Result<String> save(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    // 修改分类
    @PutMapping
    public Result<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success("修改分类成功");
    }

    // 根据ID删除分类
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success("删除分类成功");
    }

    // 分页查询分类
    @GetMapping("/page")
    public Result<Page<Category>> page(int pageNum, int pageSize) {
        Page<Category> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort); // 假设有个 sort 排序字段，没有就把这行删了
        categoryService.page(page, wrapper);
        return Result.success(page);
    }
}
