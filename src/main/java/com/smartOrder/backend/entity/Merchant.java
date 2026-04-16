package com.smartOrder.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @TableName merchant
 */
@TableName(value ="merchant")
@Data
public class Merchant implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long ownerId;

    private String phone;

    private String address;

    private String logo;

    private String description;

    private Integer status;

    private LocalDateTime createTime;

    private Integer isDeleted;

    private static final long serialVersionUID = 1L;
}