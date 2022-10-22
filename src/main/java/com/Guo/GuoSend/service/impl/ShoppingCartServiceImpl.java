package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.entity.ShoppingCart;
import com.Guo.GuoSend.mapper.ShoppingCartMapper;
import com.Guo.GuoSend.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
