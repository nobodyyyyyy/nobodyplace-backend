package com.nobody.nobodyplace.oldpojo.dao;

import com.nobody.nobodyplace.oldpojo.entity.CountDown;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountDownDAO extends JpaRepository<CountDown, Long> {
    CountDown findById(long id);
}
