package com.nobody.nobodyplace.service;

import com.nobody.nobodyplace.pojo.entity.User;
import com.nobody.nobodyplace.pojo.dto.UserLoginDTO;

public interface UserService {

    /**
     * 用户登录
     */
    User login(UserLoginDTO userLoginDTO);

//    final UserDAO userDAO;
//
//    public UserService(UserDAO userDAO) {
//        this.userDAO = userDAO;
//    }
//
//    public boolean isExist(String username) {
//        User user = getByName(username);
//        return null != user;
//    }
//
//    public User getByName(String username) {
//        return userDAO.findByUsername(username);
//    }
//
//    public User get(String username, String password){
//        return userDAO.getByUsernameAndPassword(username, password);
//    }
//
//    public void add(User user) {
//        userDAO.save(user);
//    }

}
