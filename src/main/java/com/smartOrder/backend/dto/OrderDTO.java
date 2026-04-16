package com.smartOrder.backend.dto;

import com.smartOrder.backend.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDTO {
    private Long addressId;
    private BigDecimal totalPrice;
    private Long userId;
    private List<OrderItem> itemList;
    private Long merchantId;
}
