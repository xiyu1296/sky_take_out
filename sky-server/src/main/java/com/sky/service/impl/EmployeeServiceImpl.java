package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.autoconfigure.PageHelperProperties;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.beans.beancontext.BeanContext;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private PageHelperProperties pageHelperProperties;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    public void save(EmployeeDTO employeeDTO){

        Employee emp = new Employee();
        BeanUtils.copyProperties(employeeDTO,emp);

        emp.setStatus(StatusConstant.ENABLE);
        emp.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        employeeMapper.insert(emp);


    }

    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO){

        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        Page<Employee> pg = employeeMapper.pageQuery(employeePageQueryDTO);

        long total = pg.getTotal();
        List<Employee> record= pg.getResult();

        return new PageResult(total,record);
    }

    public void startOrStop(Integer status, long id) {

        Employee emp = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(emp);

    }

    public Employee getById(long id){

        Employee emp =  employeeMapper.getById(id);
        emp.setPassword("****");

        return  emp;
    }

    public void update(EmployeeDTO employeeDTO){

        Employee emp = new Employee();
        BeanUtils.copyProperties(employeeDTO,emp);

        employeeMapper.update(emp);


    }
}
