package com.smartOrder.backend.controller;

import com.smartOrder.backend.common.Result;
import com.smartOrder.backend.entity.Address;
import com.smartOrder.backend.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;

    /**
     * 查询当前登录用户的地址列表（不再查全部，只查自己的）
     */
    @GetMapping("/list")
    public Result<List<Address>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Address> list = addressService.listByUserId(userId);
        return Result.success(list);
    }

    /**
     * 新增地址
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Address address, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        address.setUserId(userId); // 把真实用户ID塞进去
        addressService.addAddress(address);
        return Result.success("新增地址成功");
    }

    /**
     * 更新地址
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Address address) {
        addressService.updateAddress(address);
        return Result.success("更新地址成功");
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/delete")
    public Result<String> delete(@RequestParam Long id) {
        addressService.deleteAddress(id);
        return Result.success("删除地址成功");
    }

}
