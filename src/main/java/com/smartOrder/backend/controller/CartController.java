package com.smartOrder.backend.controller;

import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Cart;
import com.smartOrder.backend.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 加入购物车
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Cart cart, HttpServletRequest request) {
        // 👇 加上这两行：从测试后门/Token中拿到真实的userId，塞进cart对象
        Long userId = (Long) request.getAttribute("userId");
        cart.setUserId(userId);

        cartService.addOrUpdate(cart);
        return Result.success("已加入购物车");
    }

    /**
     * 修改购物车中某项的数量
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Cart cart) {
        cartService.updateNum(cart);
        return Result.success("修改成功");
    }

    /**
     * 删除购物车中的某一项
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        cartService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 清空某用户的购物车
     */
    @DeleteMapping("/clean")
    public Result<String> clean(HttpServletRequest request) {
        // 👇 改这里：从请求里拿，不要让前端传
        Long userId = (Long) request.getAttribute("userId");
        cartService.cleanCart(userId);
        return Result.success("购物车已清空");
    }

    /**
     * 查看购物车列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(HttpServletRequest request) {
        // 👇 改这里：从请求里拿，不要让前端传
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = cartService.getCartDetail(userId);
        return Result.success(result);
    }
}
