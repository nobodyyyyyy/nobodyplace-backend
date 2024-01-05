package com.nobody.nobodyplace.service.impl;

import com.nobody.nobodyplace.exception.AccountNotFoundException;
import com.nobody.nobodyplace.exception.PasswordErrorException;
import com.nobody.nobodyplace.pojo.entity.User;
import com.nobody.nobodyplace.mapper.UserMapper;
import com.nobody.nobodyplace.pojo.dto.UserLoginDTO;
import com.nobody.nobodyplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        User user = userMapper.getByUsername(username);
        if (user == null) {
            throw new AccountNotFoundException("账号不存在");
        }
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            throw new PasswordErrorException("密码错误");
        }
        return user;
    }
}
