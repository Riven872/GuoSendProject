package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.dto.DishDto;
import com.Guo.GuoSend.entity.Dish;
import com.Guo.GuoSend.entity.DishFlavor;
import com.Guo.GuoSend.mapper.DishMapper;
import com.Guo.GuoSend.service.DishFlavorService;
import com.Guo.GuoSend.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Resource
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时增加口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);
        //菜品保存完之后会有菜品的id
        Long dishId = dishDto.getId();
        //将每个口味的菜品id赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.forEach(e -> e.setDishId(dishId));

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }
}
