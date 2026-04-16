package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.OrderItem;
import com.smartOrder.backend.service.OrderItemService;
import com.smartOrder.backend.mapper.OrderItemMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_item(订单详情表)】的数据库操作Service实现
* @createDate 2026-04-02 22:41:14
*/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem>
    implements OrderItemService{
}




