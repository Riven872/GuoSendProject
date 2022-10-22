package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.common.CustomException;
import com.Guo.GuoSend.dto.SetmealDto;
import com.Guo.GuoSend.entity.Setmeal;
import com.Guo.GuoSend.entity.SetmealDish;
import com.Guo.GuoSend.mapper.SetmealMapper;
import com.Guo.GuoSend.service.SetmealDishService;
import com.Guo.GuoSend.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 删除套餐及其关联的菜品
     *
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getStatus, 1)
                .in(Setmeal::getId, ids);
        if (this.count(queryWrapper) > 0) {
            throw new CustomException("存在售卖状态的套餐，不可删除！");
        }

        //先删除套餐数据
        this.removeByIds(ids);

        //再删除套餐对应菜品的数据
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper();
        wrapper.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(wrapper);
    }

    /**
     * 获取套餐及其菜品信息
     *
     * @param id
     */
    @Override
    public SetmealDto getWithDish(Long id) {
        //获取套餐的基本信息
        Setmeal setmeal = this.getById(id);
        SetmealDto dto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, dto);
        //查询套餐对应的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
        dto.setSetmealDishes(dishes);
        return dto;
    }

    /**
     * 修改套餐及其菜品信息
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐的基本信息
        this.updateById(setmealDto);

        //删除套餐中对应的菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getDishId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);

        //对菜品添加套餐的信息
        setmealDto.getSetmealDishes().forEach(e -> e.setSetmealId(setmealDto.getId()));
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());
    }
}
