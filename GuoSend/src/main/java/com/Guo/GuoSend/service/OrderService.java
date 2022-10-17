package com.Guo.GuoSend.service;

import com.Guo.GuoSend.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

public interface OrderService extends IService<Orders> {
    /**
     * 用户下单
     *
     * @param orders
     */
    public void submit(Orders orders);
}
