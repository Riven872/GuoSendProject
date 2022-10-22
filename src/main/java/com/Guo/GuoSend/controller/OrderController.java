package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.BaseContext;
import com.Guo.GuoSend.common.R;
import com.Guo.GuoSend.entity.Orders;
import com.Guo.GuoSend.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    /**
     * 下单操作
     *
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 查看订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page<Orders>> page(Integer page, Integer pageSize) {
        Page<Orders> pageInfo = new Page<Orders>(page, pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId, BaseContext.getId());

        return R.success(orderService.page(pageInfo, queryWrapper));
    }
}
