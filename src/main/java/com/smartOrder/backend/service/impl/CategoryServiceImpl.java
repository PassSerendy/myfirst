package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Category;
import com.smartOrder.backend.service.CategoryService;
import com.smartOrder.backend.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【category(分类表)】的数据库操作Service实现
* @createDate 2026-04-02 22:40:52
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

}




