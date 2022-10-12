package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.dto.SetmealDto;
import com.Guo.GuoSend.entity.Setmeal;
import com.Guo.GuoSend.mapper.SetmealMapper;
import com.Guo.GuoSend.service.SetmealDishService;
import com.Guo.GuoSend.service.SetmealService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Resource
    private SetmealDishService setmealDishService;

    /**
     * 保存套餐及其菜品信息
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        this.save(setmealDto);
        //保存对应的菜品信息
        setmealDto.getSetmealDishes().forEach(e -> e.setSetmealId(setmealDto.getId()));
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());
    }
}
