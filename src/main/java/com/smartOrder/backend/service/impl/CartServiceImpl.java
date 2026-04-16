package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Cart;
import com.smartOrder.backend.entity.Dish;
import com.smartOrder.backend.exception.CustomException;
import com.smartOrder.backend.mapper.CartMapper;
import com.smartOrder.backend.service.CartService;
import com.smartOrder.backend.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private DishService dishService;

    @Override
    public void addOrUpdate(Cart cart) {
        // 1. 先检查菜品是否存在且在售
        Dish dish = dishService.getById(cart.getDishId());
        if (dish == null || dish.getStatus() != 1) {
            throw new CustomException("菜品不存在或已停售");
        }

        // 2. 查购物车里是否已有该用户该菜品的记录
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, cart.getUserId())
                .eq(Cart::getDishId, cart.getDishId());
        Cart existingCart = this.getOne(wrapper);

        if (existingCart != null) {
            existingCart.setNum(cart.getNum() != null ? cart.getNum() : 1);
            this.updateById(existingCart);
        } else {
            // 不存在 → 新增，默认数量1
            if (cart.getNum() == null) {
                cart.setNum(1);
            }
            this.save(cart);
        }
    }

    @Override
    public void updateNum(Cart cart) {
        if (cart.getId() == null) {
            throw new CustomException("请指定购物车记录ID");
        }
        if (cart.getNum() <= 0) {
            // 数量 ≤ 0 就直接删掉
            this.removeById(cart.getId());
        } else {
            this.updateById(cart);
        }
    }

    @Override
    public void cleanCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        this.remove(wrapper);
    }

    @Override
    public Map<String, Object> getCartDetail(Long userId) {
        // 1. 查出该用户所有购物车记录
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getCreateTime);
        List<Cart> cartList = this.list(wrapper);

        // 2. 组装返回数据：把购物车记录和菜品信息拼在一起
        List<Map<String, Object>> itemList = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (Cart cart : cartList) {
            Dish dish = dishService.getById(cart.getDishId());

            Map<String, Object> item = new HashMap<>();
            item.put("cartId", cart.getId());         // 购物车记录ID（用于修改数量、删除）
            item.put("dishId", dish.getId());          // 菜品ID
            item.put("dishName", dish.getName());      // 菜品名称
            item.put("price", dish.getPrice());        // 单价
            item.put("image", dish.getImage());        // 菜品图片
            item.put("num", cart.getNum());            // 数量

            // 计算小计 = 单价 × 数量
            BigDecimal subtotal = dish.getPrice().multiply(new BigDecimal(cart.getNum()));
            item.put("subtotal", subtotal);

            // 累加总价
            totalPrice = totalPrice.add(subtotal);

            itemList.add(item);
        }

        // 3. 组装最终返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("cartItems", itemList);   // 购物车列表
        result.put("totalPrice", totalPrice); // 总价

        return result;
    }
}



