package org.bs.controllers;

import org.apache.log4j.Logger;
import org.bs.model.User;

import bassis.bassis_bean.annotation.Aop;
import bassis.bassis_bean.annotation.Autowired;
import bassis.bassis_hibernate.annotation.Data;
import bassis.bassis_hibernate.tool.Dao;
import org.bassis.tools.json.GsonUtils;
import com.bassis.web.annotation.Controller;
import com.bassis.web.annotation.RequestMapping;
import com.bassis.web.assist.Resource;
import com.bassis.web.assist.StewardResource;
import com.bassis.web.work.View;

@Controller("/user")
// @Interceptor("/interceptor/privilege")
public class UserController {
	private static Logger logger = Logger.getLogger(UserController.class);
	// @Autowired(verify="NOTNULL")
	private String tel;
	@Autowired
	private User user;
	@Data
	private Dao dao;
	@Data("test2")
	private Dao daoTest2;
	
	private static Resource resource = StewardResource.get("/user");
	@RequestMapping
	public String add() {
//		dao.
		int a=dao.SQLQueryCount("select count(1) from api_operation");
		System.out.println("db:"+dao.getDbIndex()+"| "+a);
		int a2=daoTest2.SQLQueryCount("select count(1) from goods_info");
		System.out.println("db:"+daoTest2.getDbIndex()+"| "+a2);
		return "db:"+dao.getDbIndex()+"| "+a+" ****** db:"+daoTest2.getDbIndex()+"| "+a2;
	}
	@RequestMapping
	public View loginH() {
		String sendUrl = "http://www.baidu.com";
		// logger.info("user.name:"+user.getName());
		View view = new View(sendUrl, true, user);
		return view;
	}

	@RequestMapping
	public View login() {
		String sendUrl = "/controllers/EmpController/res.json";
		logger.info("user.name:" + user.getName());
		View view = new View(sendUrl, false, user);
		return view;
	}

	@RequestMapping("/res")
	@Aop("aop.UserAop")
	public String login2() {
		if (null == user) {
			user = new User();
			user.setName("李四");
		}
		logger.info("user.name:" + user.getName());
		System.out.println(resource.getServletResource().getPath());
		return GsonUtils.objectToJson(user);
	}
}
