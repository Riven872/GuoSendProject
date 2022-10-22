package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.entity.User;
import com.Guo.GuoSend.mapper.UserMapper;
import com.Guo.GuoSend.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
