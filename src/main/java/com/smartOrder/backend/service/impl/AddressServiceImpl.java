package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.entity.Address;
import com.smartOrder.backend.service.AddressService;
import com.smartOrder.backend.mapper.AddressMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author Administrator
* @description 针对表【address(地址)】的数据库操作Service实现
* @createDate 2026-04-02 22:40:40
*/
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address>
    implements AddressService{

    @Override
    public List<Address> listByUserId(Long userId) {
        return this.list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getId));
    }

    @Override
    public void addAddress(Address address){
        if(address.getIsDefault() !=null && address.getIsDefault()==1){
            Address oldDefault=this.getOne(
                    new LambdaQueryWrapper<Address>()
                            .eq(Address::getUserId,address.getUserId())
                            .eq(Address::getIsDefault,1)
            );

            if(oldDefault != null){
                oldDefault.setIsDefault(0);
                this.updateById(oldDefault);
            }
        }
        this.save(address);
    }

/**
 * 更新地址信息的方法
 * @param address 包含更新后地址信息的Address对象
 */
    @Override
    public void updateAddress(Address address){
        if (address.getIsDefault()!=null && address.getIsDefault()==1){
            Address oldDefault=this.getOne(
                    new LambdaQueryWrapper<Address>()
                            .eq(Address::getUserId,address.getUserId())
                            .eq(Address::getIsDefault,1)
            );
            if(oldDefault != null){
                oldDefault.setIsDefault(0);
                this.updateById(oldDefault);
            }
        }
        this.updateById(address);
    }

    @Override
    public void deleteAddress(Long id){
        this.removeById(id);
    }

}




