package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.BaseContext;
import com.Guo.GuoSend.common.R;
import com.Guo.GuoSend.entity.ShoppingCart;
import com.Guo.GuoSend.service.ShoppingCartService;
import com.alibaba.druid.sql.visitor.functions.If;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

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
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cart = shoppingCart;
        }
        //endregion

        return R.success(cart);
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getId())
                .orderByDesc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

    /**
     * 减少购物车中指定的物品
     *
     * @param cart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart cart) {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getId())
                .and(e -> e.eq(cart.getDishId() != null, ShoppingCart::getDishId, cart.getDishId())
                        .or()
                        .eq(cart.getSetmealId() != null, ShoppingCart::getSetmealId, cart.getSetmealId()));
        ShoppingCart shoppingCart = shoppingCartService.getOne(queryWrapper);
        if (shoppingCart != null) {
            //数量减一
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartService.updateById(shoppingCart);
            //如果数量减去后是0，则删除该数据
            if (shoppingCart.getNumber() <= 0) {
                shoppingCartService.removeById(shoppingCart.getId());
                return R.success(new ShoppingCart());
            }
        }


        return R.success(shoppingCart);
    }
}
