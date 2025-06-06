package com.library.service;

import com.library.domain.Employee;
import com.library.entity.vo.ApiResult;

import java.util.List;

public interface EmployeeService {
    ApiResult getEmployeeById(Long id);
    ApiResult getAllEmployees();
    ApiResult addEmployee(Employee employee);
    ApiResult updateEmployee(Employee employee);
    ApiResult deleteEmployee(Long id);
}