package com.Guo.GuoSend.service;

import com.Guo.GuoSend.dto.SetmealDto;
import com.Guo.GuoSend.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 保存套餐及其菜品信息
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);
}
