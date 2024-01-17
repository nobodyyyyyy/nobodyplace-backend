package com.nobody.nobodyplace.service.old;

import com.nobody.nobodyplace.oldpojo.dao.CountDownDAO;
import com.nobody.nobodyplace.oldpojo.entity.CountDown;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountDownService {
    final CountDownDAO countDownDAO;

    public CountDownService(CountDownDAO countDownDAO) {
        this.countDownDAO = countDownDAO;
    }

    public CountDown getById(long id) {
        return countDownDAO.findById(id);
    }

    public List<CountDown> getAllCountDowns() {
        return countDownDAO.findAll();
    }

    public void addOrUpdate(CountDown countDown) {
        countDownDAO.save(countDown);
    }

    public void deleteCountDownById(long id) {
        countDownDAO.deleteById(id);
    }
}
