package com.smartOrder.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @TableName log
 */
@TableName(value ="log")
@Data
public class Log implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Integer action;

    private String url;

    private String method;

    private String ip;

    private LocalDateTime time;

    private static final long serialVersionUID = 1L;
}