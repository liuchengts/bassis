//package org.bs.controllers;
//
//import org.apache.log4j.Logger;
//import org.bs.model.User;
//
//import bassis.bassis_bean.annotation.Autowired;
//import org.bassis.tools.json.GsonUtils;
//import com.bassis.web.annotation.Controller;
//import com.bassis.web.annotation.RequestMapping;
//
//@Controller
//public class EmpController {
//	private static Logger logger = Logger.getLogger(EmpController.class);
//	@Autowired
//	private String tel;
//	@Autowired
//	private User user;
//
//	@RequestMapping("/res")
//	public String login2() {
//		logger.info("user.name:"+user.getName());
//		return GsonUtils.objectToJson(user);
//	}
//}
