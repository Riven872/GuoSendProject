package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.common.BaseContext;
import com.Guo.GuoSend.common.CustomException;
import com.Guo.GuoSend.entity.*;
import com.Guo.GuoSend.mapper.OrderMapper;
import com.Guo.GuoSend.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {
    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private UserService userService;

    @Resource
    private AddressBookService addressBookService;

    @Resource
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param orders
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        Long userId = BaseContext.getId();//用户id

        //region 查询用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> shoppingCartWrapper = new LambdaQueryWrapper();
        shoppingCartWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartWrapper);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new CustomException("购物车为空，不能下单！");
        }
        //endregion

        //region 查询用户数据
        User user = userService.getById(userId);
        //endregion

        //region 查询用户地址数据
        AddressBook addr = addressBookService.getById(orders.getAddressBookId());
        if (addr == null) {
            throw new CustomException("用户地址有误，不能下单");
        }
        //endregion

        //region 计算总金额并将数据封装到明细表中
        long id = IdWorker.getId();//订单号
        List<OrderDetail> orderDetails = new ArrayList<>();
        AtomicInteger atomicInteger = new AtomicInteger();//线程安全的累加操作
        shoppingCarts.forEach(e -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);//关联订单号
            orderDetail.setNumber(e.getNumber());//商品份数
            orderDetail.setDishFlavor(e.getDishFlavor());//口味
            orderDetail.setDishId(e.getDishId());//菜品id
            orderDetail.setSetmealId(e.getSetmealId());//套餐id
            orderDetail.setName(e.getName());//商品名称
            orderDetail.setImage(e.getImage());//商品图片
            orderDetail.setAmount(e.getAmount());//商品单价
            atomicInteger.addAndGet(e.getAmount().multiply(new BigDecimal(e.getNumber())).intValue());//单价*数量
            orderDetails.add(orderDetail);
        });
        orderDetailService.saveBatch(orderDetails);
        //endregion

        //region 向订单表插入数据
        orders.setId(id);//订单表id
        orders.setNumber(String.valueOf(id));//订单号
        orders.setOrderTime(LocalDateTime.now());//下单时间
        orders.setCheckoutTime(LocalDateTime.now());//支付时间
        orders.setAmount(new BigDecimal(atomicInteger.intValue()));//订单总金额
        orders.setUserId(userId);//用户id
        orders.setUserName(user.getName());//用户名
        orders.setConsignee(addr.getConsignee());//收货人
        orders.setPhone(addr.getPhone());//收货人手机号
        orders.setAddress(addr.getDetail());//收货地址
        this.save(orders);
        //endregion

        //region 清空购物车数据
        shoppingCartService.remove(shoppingCartWrapper);
        //endregion
    }
}
