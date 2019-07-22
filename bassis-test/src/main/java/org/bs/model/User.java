package org.bs.model;

import bassis.bassis_bean.annotation.Autowired;
import bassis.bassis_bean.annotation.Component;
@Component
public class User {
	@Autowired
	private String name;
	@Autowired
	private String pwd;
	@Autowired
	private int age;
	@Autowired
	private Emp emp;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Emp getEmp() {
		return emp;
	}
	public void setEmp(Emp emp) {
		this.emp = emp;
	}
	
	
}
