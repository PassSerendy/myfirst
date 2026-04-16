package com.smartOrder.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @TableName user_behavior
 */
@TableName(value ="user_behavior")
@Data
public class UserBehavior implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long dishId;

    private Integer action;

    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
}