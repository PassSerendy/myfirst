package com.smartOrder.backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.dto.OrderDTO;
import com.smartOrder.backend.service.OrdersService;
import com.smartOrder.backend.vo.OrderVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService orderService;

    /**
     * 提交订单（从Token里拿userId，不再硬编码）
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderDTO.setUserId(userId); // 把真实用户ID传给 Service
        orderService.submitOrder(orderDTO);
        return Result.success("下单成功");
    }

    /**
     * 用户分页订单列表
     */
    @GetMapping("/userPage")
    public Result<IPage<OrderVO>> userPage(Integer PageNum, Integer PageSize, HttpServletRequest request) {
        if (PageNum == null) PageNum = 1;
        if (PageSize == null) PageSize = 10;
        Long userId = (Long) request.getAttribute("userId");
        IPage<OrderVO> page = orderService.getUserOrderPage(PageNum, PageSize, userId);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    @PutMapping("/changeStatus")
    public Result<String> changeStatus(@RequestBody Map<String, Integer> map) {
        Long orderId = map.get("id").longValue();
        Integer status = map.get("status");
        orderService.updateOrderStatus(orderId, status);
        return Result.success("状态修改成功");
    }

    @GetMapping("/merchantPage")
    public Result<IPage<OrderVO>> merchantPage(Integer pageNum, Integer pageSize, Integer status) {
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;
        IPage<OrderVO> page = orderService.getMerchantOrderPage(pageNum, pageSize, status);
        return Result.success(page);
    }
}
