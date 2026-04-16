package com.smartOrder.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @TableName payment
 */
@TableName(value ="payment")
@Data
public class Payment implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String transactionId;

    private Integer payType;

    private Double amount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime payTime;

    private static final long serialVersionUID = 1L;
}