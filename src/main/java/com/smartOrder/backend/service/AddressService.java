package com.smartOrder.backend.service;

import com.smartOrder.backend.entity.Address;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【address(地址)】的数据库操作Service
* @createDate 2026-04-02 22:40:40
*/
public interface AddressService extends IService<Address> {
    void addAddress(Address address);

    void updateAddress(Address address);

    void deleteAddress(Long id);

    List<Address> listByUserId(Long userId);
}
