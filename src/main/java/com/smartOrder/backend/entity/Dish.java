package com.smartOrder.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @TableName dish
 */
@TableName(value ="dish")
@Data
public class Dish implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private BigDecimal price;

    private Long categoryId;

    private Long merchantId;

    private String image;

    private String description;

    private Integer stock;

    private Integer status;

    private LocalDateTime createTime;

    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}