package com.Guo.GuoSend.controller;

import com.Guo.GuoSend.common.R;
import com.Guo.GuoSend.entity.Employee;
import com.Guo.GuoSend.service.EmployeeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;import java.time.LocalDateTime;

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
        httpServletRequest.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest) {
        //清理Session中保存的当前登录员工的id
        httpServletRequest.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，信息为{}", employee);

        //设置初始密码为123，且用MD5进行加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        ////设置初始创建时间
        //employee.setCreateTime(LocalDateTime.now());
        ////设置初始更新时间
        //employee.setUpdateTime(LocalDateTime.now());
        //从session中获取当前登录用户的id
        //long empId = (Long) request.getSession().getAttribute("employee");
        ////设置初始创建人
        //employee.setCreateUser(empId);
        ////设置最新修改人
        //employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 分页查询员工数据
     *
     * @param page     当前页码
     * @param pageSize 每页的数据量
     * @param name     通过名字快速搜索
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件（快速搜索）构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件，当name字段不为空时才会添加like条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //用来屏蔽超管账号，通过匹配Username即账号名去实现
        queryWrapper.ne(Employee::getUsername, "admin");
        //执行查询
        employeeService.page(pageInfo, queryWrapper);
        //执行成功
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        //long empId = (long) httpServletRequest.getSession().getAttribute("employee");//获取当前登录人的id
        //employee.setUpdateUser(empId);//设置更新人为当前登录人
        //employee.setUpdateTime(LocalDateTime.now());//设置修改时间为当前时间
        employeeService.updateById(employee);//调用employeeService的update方法

        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id){
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有查询到对应的员工信息");
    }
}
