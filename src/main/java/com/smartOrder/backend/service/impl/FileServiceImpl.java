package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.File;
import com.smartOrder.backend.service.FileService;
import com.smartOrder.backend.mapper.FileMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【file(文件表)】的数据库操作Service实现
* @createDate 2026-04-02 22:41:05
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File>
    implements FileService{

}




