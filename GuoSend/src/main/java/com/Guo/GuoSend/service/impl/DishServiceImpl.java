package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.entity.Dish;
import com.Guo.GuoSend.mapper.DishMapper;
import com.Guo.GuoSend.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
