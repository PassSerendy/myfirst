package com.smartOrder.backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * @TableName file
 */
@TableName(value ="file")
@Data
public class File implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String url;

    private Integer type;

    private Integer size;

    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
}