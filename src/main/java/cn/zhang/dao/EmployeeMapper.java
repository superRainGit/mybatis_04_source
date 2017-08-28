package cn.zhang.dao;

import java.util.List;

import cn.zhang.beans.Employee;

public interface EmployeeMapper {
	
	public Employee getEmpById(Integer id);
	
	public List<Employee> getEmps();
	
	public void addEmp(Employee emp);

}
