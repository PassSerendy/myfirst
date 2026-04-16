package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Log;
import com.smartOrder.backend.service.LogService;
import com.smartOrder.backend.mapper.LogMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【log(日志表)】的数据库操作Service实现
* @createDate 2026-04-02 22:41:08
*/
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log>
    implements LogService{

}




