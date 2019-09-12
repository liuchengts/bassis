package org.bs.controllers;

import com.bassis.bean.annotation.Autowired;
import com.bassis.boot.web.annotation.Controller;
import com.bassis.boot.web.annotation.RequestMapping;
import com.bassis.boot.web.annotation.RequestParam;
import org.bs.service.UserService;

@Controller("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/add")
    public String add(@RequestParam(required = false) String ds) {
        return userService.add(ds) + this.getClass().getSimpleName();
    }
}
