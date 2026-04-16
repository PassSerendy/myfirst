package com.smartOrder.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smartOrder.backend.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【dish】的数据库操作Service
* @createDate 2026-04-02 22:40:59
*/
public interface DishService extends IService<Dish> {
    //分页查询菜品
    IPage<Dish> getDishPage(int pageNum,int pageSize,String name,Long merchantId);

}
