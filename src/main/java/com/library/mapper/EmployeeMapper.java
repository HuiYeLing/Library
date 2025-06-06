package com.library.mapper;

import com.library.domain.Employee;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    @Select("SELECT * FROM employees WHERE id = #{id}")
    Employee getEmployeeById(@Param("id") long id);

    @Select("SELECT * FROM employees")
    List<Employee> getAllEmployees();

    @Insert("INSERT INTO employees (name, gender, phone, hire_date, age, salary) VALUES (#{name}, #{gender}, #{phone}, #{hireDate}, #{age}, #{salary})")
    int insertEmployee(Employee employee);

    @Update("UPDATE employees SET name=#{name}, gender=#{gender}, phone=#{phone}, hire_date=#{hireDate}, age=#{age}, salary=#{salary} WHERE id=#{id}")
    int updateEmployee(Employee employee);

    @Delete("DELETE FROM employees WHERE id = #{id}")
    int deleteEmployee(@Param("id") long id);
}