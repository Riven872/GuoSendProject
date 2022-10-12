package com.Guo.GuoSend.service;

import com.Guo.GuoSend.dto.DishDto;
import com.Guo.GuoSend.entity.Dish;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

public interface DishService extends IService<Dish> {
    /**
     * 新增菜品，同时增加口味数据
     * @param dishDto
     */
    public void saveWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和对应口味信息
     * @param id
     * @return
     */
    public DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品信息及其口味
     * @param dishDto
     */
    public void updateWithFlavor(DishDto dishDto);
}
