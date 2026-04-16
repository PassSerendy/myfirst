package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Dish;
import com.smartOrder.backend.mapper.CategoryMapper;
import com.smartOrder.backend.service.DishService;
import com.smartOrder.backend.mapper.DishMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author Administrator
* @description 针对表【dish】的数据库操作Service实现
* @createDate 2026-04-02 22:40:59
*/
@Service  // 标记该类为Spring服务层组件，用于处理业务逻辑
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
    implements DishService {  // 实现DishService接口，定义具体的菜品业务逻辑处理

    /**
     * 分页查询菜品信息
     *
     * @param pageNum  当前页码
     * @param pageSize 每页显示数量
     * @param name     菜品名称(可选，用于模糊查询)
     * @return 返回分页结果，包含菜品数据
     */
    @Override  // 标记为重写接口中的方法
    public IPage<Dish> getDishPage(int pageNum, int pageSize, String name, Long merchantId) {
        // 创建分页对象，设置当前页码和每页显示数量
        Page<Dish> page = new Page<>(pageNum, pageSize);
        // 创建Lambda查询条件构造器
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        // 如果name不为空，则添加名称模糊查询条件
        wrapper.like(StringUtils.hasText(name), Dish::getName, name);
        // 按创建时间降序排序
        wrapper.orderByDesc(Dish::getCreateTime);
        wrapper.eq(merchantId != null, Dish::getMerchantId, merchantId);
        wrapper.like(name != null && !name.isEmpty(), Dish::getName, name);
        wrapper.orderByDesc(Dish::getCreateTime);
        // 执行分页查询并返回结果
        return this.page(page, wrapper);
    }

    // 注入菜品数据访问层对象
    @Resource
    private DishMapper dishMapper;
    // 注入分类数据访问层对象
    @Resource
    private CategoryMapper categoryMapper;
}

