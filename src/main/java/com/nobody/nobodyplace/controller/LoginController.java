package com.nobody.nobodyplace.controller;


import com.nobody.nobodyplace.pojo.entity.User;
import com.nobody.nobodyplace.pojo.dto.UserLoginDTO;
import com.nobody.nobodyplace.pojo.vo.UserLoginVO;
import com.nobody.nobodyplace.properties.JwtProperties;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.UserService;
import com.nobody.nobodyplace.utils.Constant;
import com.nobody.nobodyplace.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    private static final Logger Nlog = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService;

    private final JwtProperties jwtProperties;

    public LoginController(UserService userService, JwtProperties jwtProperties) {
        this.userService = userService;
        this.jwtProperties = jwtProperties;
    }

//    @CrossOrigin
//    @PostMapping(value = API.LOGIN)
//    @ResponseBody
//    @Deprecated
//    public ResultPast login(@RequestBody User requestUser, HttpSession session) {
//        String username = requestUser.getUsername();
//        username = HtmlUtils.htmlEscape(username);
//
//        User user = userService.get(username, requestUser.getPassword());
//        if (null == user) {
//            return new ResultPast(400);
//        } else {
//            session.setAttribute("user", user);
//            return new ResultPast(0);
//        }
//    }

    @PostMapping(value = API.LOGIN)
    @CrossOrigin
    @ResponseBody
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        Nlog.info("User Login：{}", userLoginDTO);

        User user = userService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .name(user.getName())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

}

