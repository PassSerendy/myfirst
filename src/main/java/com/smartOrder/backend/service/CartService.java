package com.smartOrder.backend.service;

import com.smartOrder.backend.entity.Cart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
* @author Administrator
* @description 针对表【cart(购物车表)】的数据库操作Service
* @createDate 2026-04-02 22:40:49
*/
public interface CartService extends IService<Cart> {
    /**
     * 加入购物车（已存在则数量+num，不存在则新增）
     */
    void addOrUpdate(Cart cart);

    /**
     * 修改购物车中某项的数量
     */
    void updateNum(Cart cart);

    /**
     * 清空某用户的购物车
     */
    void cleanCart(Long userId);

    /**
     * 查看购物车详情（带菜品名称、价格、小计、总价）
     */
    Map<String, Object> getCartDetail(Long userId);

}
