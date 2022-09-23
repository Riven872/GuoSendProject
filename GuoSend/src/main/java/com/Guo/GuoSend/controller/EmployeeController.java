package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.R;
import com.Guo.GuoSend.entity.Employee;
import com.Guo.GuoSend.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.Request;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Resource
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param httpServletRequest 将登录信息写入session中
     * @param employee           映射请求的参数
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        //1、将页面提交的密码password进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("用户名不存在");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus().equals(0)) {
            return R.error("该员工状态已被禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        httpServletRequest.getSession().setAttribute("employee", employee.getId());
        return R.success(emp);
    }
}
