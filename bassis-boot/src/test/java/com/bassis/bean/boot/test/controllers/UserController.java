package com.bassis.bean.boot.test.controllers;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.boot.test.service.UserService;
import com.bassis.boot.web.annotation.Controller;
import com.bassis.boot.web.annotation.RequestMapping;

@Controller("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/add")
    public String add(String name) {
        return userService.add(name) + "c";
    }
}
