package com.nobody.nobodyplace.DAO;


import com.nobody.nobodyplace.request.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User,Integer> {
    User findByUsername(String username);

    User getByUsernameAndPassword(String username, String password);
}
