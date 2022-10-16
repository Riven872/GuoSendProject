package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.BaseContext;
import com.Guo.GuoSend.common.R;
import com.Guo.GuoSend.entity.ShoppingCart;
import com.Guo.GuoSend.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Resource
    private ShoppingCartService shoppingCartService;

    /**
     * 将选择的物品添加到购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //region 设置购物车的用户
        shoppingCart.setUserId(BaseContext.getId());
        //endregion

        //region 查询是否已经存在
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, shoppingCart.getUserId())
                .and(e -> e.eq(shoppingCart.getDishId() != null, ShoppingCart::getDishId, shoppingCart.getDishId())
                        .or()
                        .eq(shoppingCart.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCart.getSetmealId()));
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        //endregion

        //region 存在则数量加一，不存在则默认数量一
        if (cart != null) {
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartService.updateById(cart);
        } else {
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }
        //endregion

        return R.success(cart);
    }

    @GetMapping("/list")
    public R<String> foo() {
        return null;
    }
}
