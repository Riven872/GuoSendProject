package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.common.CustomException;
import com.Guo.GuoSend.entity.Category;
import com.Guo.GuoSend.entity.Dish;
import com.Guo.GuoSend.entity.Setmeal;
import com.Guo.GuoSend.mapper.CategoryMapper;
import com.Guo.GuoSend.service.CategoryService;
import com.Guo.GuoSend.service.DishService;
import com.Guo.GuoSend.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;

    /**
     * 根据id删除分类，在删除之前判断是否关联了菜品或套餐
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，则删除时抛出一个业务异常
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishQueryWrapper);
        if (count1 > 0) {
            throw new CustomException("当前分类下已经关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，则删除时抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setMealQueryWrapper = new LambdaQueryWrapper<>();
        setMealQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setMealQueryWrapper);
        if (count2 > 0) {
            throw new CustomException("当前分类下已经关联了套餐，不能删除");
        }
        //没有关联，则正常删除
        this.removeById(id);
    }
}
