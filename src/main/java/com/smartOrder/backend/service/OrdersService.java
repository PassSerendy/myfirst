package com.smartOrder.backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.smartOrder.backend.dto.OrderDTO;
import com.smartOrder.backend.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.smartOrder.backend.vo.OrderVO;

/**
* @author Administrator
* @description 针对表【orders(订单)】的数据库操作Service
* @createDate 2026-04-02 22:41:16
*/
public interface OrdersService extends IService<Orders> {
    void submitOrder(OrderDTO orderDTO);

    IPage<OrderVO> getUserOrderPage(int pageNum, int pageSize,Long userId);

    OrderVO getOrderDetail(Long orderId);

    void updateOrderStatus(Long orderId,Integer status);

    IPage<OrderVO> getMerchantOrderPage(int pageNum, int pageSize, Integer status);
}
