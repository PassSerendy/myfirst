package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Favorite;
import com.smartOrder.backend.service.FavoriteService;
import com.smartOrder.backend.mapper.FavoriteMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【favorite(用户收藏表)】的数据库操作Service实现
* @createDate 2026-04-02 22:41:03
*/
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite>
    implements FavoriteService{

}




