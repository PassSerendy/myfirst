package com.smartOrder.backend.controller;

import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Merchant;
import com.smartOrder.backend.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    // 根据ID获取商家详情（前端进入首页时调用）
    @GetMapping("/{id}")
    public Result<Merchant> getById(@PathVariable Long id) {
        Merchant merchant = merchantService.getById(id);
        return Result.success(merchant);
    }

    // 修改商家信息（比如老板修改店铺公告、logo，后续可加商家权限拦截）
    @PutMapping
    public Result<String> update(@RequestBody Merchant merchant) {
        merchantService.updateById(merchant);
        return Result.success("修改商家信息成功");
    }
}
