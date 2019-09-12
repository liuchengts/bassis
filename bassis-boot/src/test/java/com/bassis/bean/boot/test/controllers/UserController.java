package com.bassis.bean.boot.test.controllers;

import com.bassis.bean.annotation.Autowired;
import com.bassis.bean.boot.test.service.UserService;
import com.bassis.boot.web.annotation.Controller;
import com.bassis.boot.web.annotation.RequestMapping;
import com.bassis.boot.web.annotation.RequestParam;

@Controller("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/add")
    public String add(@RequestParam(required = false) String ds) {
        return userService.add(ds) + "c";
    }
}
