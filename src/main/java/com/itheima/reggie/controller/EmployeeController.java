package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import com.itheima.reggie.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

import java.time.LocalDateTime;

/**
 * @author XTL117
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpSession session,
                             @RequestBody Employee employee) {
        //1.将获取的密码弄成mdk5形式
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.去数据库查找东西
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有账号登录失败
        if (emp == null){
            return R.error("登录失败 ");
        }

        //4.如果密码不对登录失败
        if(!password.equals(emp.getPassword())){
            return R.error("登录失败");

        }

        //5.如果账号处于封禁状态，登录失败
        if (emp.getStatus()==0){
            return R.error("登录失败");
        }

        //6.登录成功,将员工id存入session
        session.setAttribute("employee",emp.getId());
        return R.success(emp);

    }

    @PostMapping("/logout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("employee");
        return R.success("");
    }

    @PostMapping
    public R<String> save(HttpSession session,
                          @RequestBody Employee employee){

        log.info("员工的信息为{}",employee.toString());
        //1.设置初始密码并且加密md5
        employee.setPassword(DigestUtils.md5DigestAsHex("12345".getBytes()));

        //2.完善剩余信息
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户id
//        Long empId= (Long) session.getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);

        return R.success("新增员工成功~");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(@RequestBody Employee employee,
                            HttpSession session){
        //更新日期
     employee.setUpdateTime(LocalDateTime.now());
//        //更新人
//        Long empId = (Long) session.getAttribute("employee");
//        employee.setUpdateUser(empId);
//        //更新
       employeeService.updateById(employee);
        return R.success("修改成功");

    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }
}
