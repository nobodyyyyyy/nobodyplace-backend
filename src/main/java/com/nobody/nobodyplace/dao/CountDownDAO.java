package com.nobody.nobodyplace.dao;

import com.nobody.nobodyplace.entity.CountDown;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountDownDAO extends JpaRepository<CountDown, Long> {
    CountDown findById(long id);
}
