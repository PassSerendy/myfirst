package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Merchant;
import com.smartOrder.backend.service.MerchantService;
import com.smartOrder.backend.mapper.MerchantMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【merchant(商家表)】的数据库操作Service实现
* @createDate 2026-04-02 22:41:11
*/
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant>
    implements MerchantService{

}




