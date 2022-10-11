package com.Guo.GuoSend.service;

import com.Guo.GuoSend.dto.DishDto;
import com.Guo.GuoSend.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时增加口味数据
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto);
}
