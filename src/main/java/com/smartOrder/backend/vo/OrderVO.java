package com.smartOrder.backend.vo;

import com.smartOrder.backend.entity.OrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private String orderNo;
    private BigDecimal totalPrice;
    private Integer status;
    private LocalDateTime createTime;

    private List<OrderItem> orderItems;
}
