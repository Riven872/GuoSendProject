package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.entity.Employee;
import com.Guo.GuoSend.mapper.EmployeeMapper;
import com.Guo.GuoSend.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
