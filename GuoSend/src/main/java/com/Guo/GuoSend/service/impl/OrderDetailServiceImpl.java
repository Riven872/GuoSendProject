package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.entity.OrderDetail;
import com.Guo.GuoSend.mapper.OrderDetailMapper;
import com.Guo.GuoSend.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
