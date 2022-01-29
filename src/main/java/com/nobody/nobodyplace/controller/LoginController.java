package com.nobody.nobodyplace.controller;


import com.nobody.nobodyplace.entity.User;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private static final Logger Nlog = LoggerFactory.getLogger(LoginController.class);
    final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @CrossOrigin
    @PostMapping(value = API.LOGIN)
    @ResponseBody
    public Result login(@RequestBody User requestUser, HttpSession session) {
        String username = requestUser.getUsername();
        username = HtmlUtils.htmlEscape(username);

        User user = userService.get(username, requestUser.getPassword());
        if (null == user) {
            return new Result(400);
        } else {
            session.setAttribute("user", user);
            return new Result(200);
        }
    }
}

