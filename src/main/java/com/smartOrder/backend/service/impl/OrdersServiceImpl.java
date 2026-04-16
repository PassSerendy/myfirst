package com.smartOrder.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smartOrder.backend.dto.OrderDTO;
import com.smartOrder.backend.entity.Dish;
import com.smartOrder.backend.entity.OrderItem;
import com.smartOrder.backend.entity.Orders;
import com.smartOrder.backend.service.CartService;
import com.smartOrder.backend.service.DishService;
import com.smartOrder.backend.service.OrderItemService;
import com.smartOrder.backend.service.OrdersService;
import com.smartOrder.backend.mapper.OrdersMapper;
import com.smartOrder.backend.vo.OrderVO;
import org.mockito.internal.matchers.Or;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartOrder.backend.exception.CustomException;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【orders(订单)】的数据库操作Service实现
* @createDate 2026-04-02 22:41:16
*/
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
    implements OrdersService{
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private CartService cartService;

    @Override
    @Transactional(rollbackFor = Exception.class) // 修复坑3：加上 rollbackFor
    public void submitOrder(OrderDTO orderDTO){
        //组装订单主表数据
        Orders order = new Orders();
        order.setUserId(orderDTO.getUserId());
        order.setAddressId(orderDTO.getAddressId());
        order.setMerchantId(orderDTO.getMerchantId());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setStatus(0);//0表示待支付

        // 修复坑1：生成更安全的唯一订单号（年月日时分秒 + 6位随机数）
        String orderNo = String.format("%tY%<tm%<td%<tH%<tM%<tS", System.currentTimeMillis())
                + String.format("%06d", new Random().nextInt(1000000));
        order.setOrderNo(orderNo);

        //存入orders表
        this.save(order);
        Long orderId = order.getId();

        //处理订单明细表
        List<OrderItem> itemList = orderDTO.getItemList();

        // 修复坑2：遍历前端传来的每一个菜品项，校验并扣减库存
        for(OrderItem item : itemList){
            item.setOrderId(orderId);

            // 1. 查出当前菜品
            Dish dish = dishService.getById(item.getDishId());
            if(dish == null || dish.getStatus() == 0){
                throw new CustomException("菜品[" + (dish==null?"未知":dish.getName()) + "]已停售");
            }
            // 2. 判断库存够不够
            if(dish.getStock() < item.getNum()){
                throw new CustomException("菜品[" + dish.getName() + "]库存不足，剩余:" + dish.getStock());
            }
            // 3. 扣减库存
            dish.setStock(dish.getStock() - item.getNum());
            dishService.updateById(dish);
        }

        //批量保存明细
        orderItemService.saveBatch(itemList);

        cartService.cleanCart(orderDTO.getUserId());

    }



    @Autowired
    private DishService dishService;

    @Override
    /**
     * 获取用户订单分页信息
     * @param pageNum 当前页码
     * @param pageSize 每页显示数量
     * @return 返回包含订单详细信息的分页结果
     */
    public IPage<OrderVO> getUserOrderPage(int pageNum, int pageSize,Long userId){
        //查主表
        Page<Orders> orderPage =new Page<>(pageNum,pageSize);

        //构建查询条件：只查用户id为1的，按创建时间倒序
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByDesc(Orders::getCreateTime);//倒序排列

        this.page(orderPage,queryWrapper);

        //转换：把Page<Orders>转成Page<OrderVO>
        IPage<OrderVO> voPage =new Page<>(orderPage.getCurrent(),orderPage.getSize(),orderPage.getTotal());

        List<OrderVO> voList = orderPage.getRecords().stream().map(order ->{
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order,vo);

            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId,order.getId());
            List<OrderItem> items = orderItemService.list(itemWrapper);

            for(OrderItem item : items){
                Dish dish =dishService.getById(item.getDishId());
                if(dish != null){
                    item.setDishName(dish.getName());
                }
            }

            vo.setOrderItems(items);

            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public OrderVO getOrderDetail(Long orderId){
        //查主表
        Orders order =this.getById(orderId);
        if(order== null){
            throw new CustomException("订单不存在");
        }

        //转vo
        OrderVO vo =new OrderVO();
        BeanUtils.copyProperties(order,vo);

        //查明细并塞入菜名
        LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemService.list(itemWrapper);

        for (OrderItem item : items) {
            Dish dish = dishService.getById(item.getDishId());
            if (dish != null) {
                item.setDishName(dish.getName());
            }
        }
        vo.setOrderItems(items);

        return vo;
    }

    @Override
    public void updateOrderStatus(Long orderId,Integer status){
        //查出订单
        Orders order =this.getById(orderId);
        if(order ==null){
            throw new CustomException("订单不存在");
        }

        //状态校验
        if(order.getStatus()==3){
            throw new CustomException("订单已取消，无法修改状态");
        }

        //更新状态
        order.setStatus(status);
        this.updateById(order);
    }

    @Override
    public IPage<OrderVO> getMerchantOrderPage(int pageNum, int pageSize, Integer status) {
        // 1. 查主表
        Page<Orders> orderPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();

        // ★ 唯一的区别：不查 userId，如果有传 status 就加上 status 条件
        if (status != null) {
            queryWrapper.eq(Orders::getStatus, status);
        }
        queryWrapper.orderByDesc(Orders::getCreateTime);

        this.page(orderPage, queryWrapper);

        // 2. 转 VO（和 userPage 后半段 100% 一样，直接复制）
        IPage<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> voList = orderPage.getRecords().stream().map(order -> {
            OrderVO vo = new OrderVO();
            BeanUtils.copyProperties(order, vo);

            LambdaQueryWrapper<OrderItem> itemWrapper = new LambdaQueryWrapper<>();
            itemWrapper.eq(OrderItem::getOrderId, order.getId());
            List<OrderItem> items = orderItemService.list(itemWrapper);

            for (OrderItem item : items) {
                Dish dish = dishService.getById(item.getDishId());
                if (dish != null) {
                    item.setDishName(dish.getName());
                }
            }
            vo.setOrderItems(items);
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

}






