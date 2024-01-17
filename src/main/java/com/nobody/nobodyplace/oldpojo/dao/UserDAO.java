package com.nobody.nobodyplace.oldpojo.dao;

import com.nobody.nobodyplace.oldpojo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    User getByUsernameAndPassword(String username, String password);
}
