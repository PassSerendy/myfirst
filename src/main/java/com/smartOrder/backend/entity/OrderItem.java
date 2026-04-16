package com.smartOrder.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * @TableName order_item
 */
@TableName(value ="order_item")
@Data
public class OrderItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long dishId;

    private BigDecimal price;

    private Integer num;

    @TableField(exist = false)
    private String dishName;

    private static final long serialVersionUID = 1L;


}