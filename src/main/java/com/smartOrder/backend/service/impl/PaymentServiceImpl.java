package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Payment;
import com.smartOrder.backend.service.PaymentService;
import com.smartOrder.backend.mapper.PaymentMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【payment(支付流水表)】的数据库操作Service实现
* @createDate 2026-04-02 22:41:19
*/
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment>
    implements PaymentService{

}




