package com.smartOrder.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @TableName category
 */
@TableName(value ="category")
@Data
public class Category implements Serializable {
    @TableId(type= IdType.AUTO)
    private Long id;

    private String name;

    private Long merchantId;

    private String sort;

    private Integer status;

    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
}