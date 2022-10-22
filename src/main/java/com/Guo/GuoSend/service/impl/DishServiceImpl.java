package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.dto.DishDto;
import com.Guo.GuoSend.entity.Dish;
import com.Guo.GuoSend.entity.DishFlavor;
import com.Guo.GuoSend.mapper.DishMapper;
import com.Guo.GuoSend.service.DishFlavorService;
import com.Guo.GuoSend.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
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

    /**
     * 根据id查询菜品信息和对应口味信息
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);

        //拷贝对象（源，目的对象）
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品对应的口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        //将查出的口味信息赋值到自定义model中
        dishDto.setFlavors(list);

        return dishDto;
    }

    /**
     * 修改菜品信息及其口味
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本数据
        this.updateById(dishDto);
        //清理当前菜品对应口味的数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //重新添加口味数据
        dishDto.getFlavors().forEach(e -> e.setDishId(dishDto.getId()));
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }
}
