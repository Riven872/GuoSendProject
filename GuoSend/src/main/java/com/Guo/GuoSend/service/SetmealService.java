package com.Guo.GuoSend.service;

import com.Guo.GuoSend.dto.SetmealDto;
import com.Guo.GuoSend.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存套餐及其菜品信息
     *
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐及其关联的菜品
     *
     * @param ids
     */
    public void removeWithDish(List<Long> ids);

    /**
     * 修改套餐及其菜品信息
     *
     * @param setmealDto
     */
    public void updateWithDish(SetmealDto setmealDto);

    /**
     * 获取套餐及其菜品信息
     *
     * @param id
     */
    public SetmealDto getWithDish(Long id);
}
