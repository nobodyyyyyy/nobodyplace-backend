package com.nobody.nobodyplace.controller;

import com.nobody.nobodyplace.oldpojo.entity.CountDown;
import com.nobody.nobodyplace.response.old.CountDownData;
import com.nobody.nobodyplace.response.old.ResultPast;
import com.nobody.nobodyplace.service.old.CountDownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CountDownController {

    private static final Logger Nlog = LoggerFactory.getLogger(CountDownController.class);
    final CountDownService service;

    public CountDownController(CountDownService service) {
        this.service = service;
    }

    @CrossOrigin
    @PostMapping(value = API.ADD_COUNTDOWN)
    @ResponseBody
    public ResultPast addCountDown(@RequestBody CountDown countDown) {
        return addOrUpdateCountDown(API.ADD_COUNTDOWN, countDown);
    }

    @CrossOrigin
    @PostMapping(value = API.UPDATE_COUNTDOWN)
    @ResponseBody
    public ResultPast updateCountDown(@RequestBody CountDown countDown) {
        return addOrUpdateCountDown(API.UPDATE_COUNTDOWN, countDown);
    }

    private ResultPast addOrUpdateCountDown(String action, CountDown countDown) {
        try {
            service.addOrUpdate(countDown);
        } catch (Exception e) {
            Nlog.info("addOrUpdateCountDown... action = " + action + ", err = " + e);
            return generateResult(-1, action, "addOrUpdateCountDown err", null);
        }
        return generateResult(0, action, "success", null);
    }

    @CrossOrigin
    @ResponseBody
    @GetMapping(value = API.GET_COUNTDOWNS)
    public ResultPast getAllCountDowns() {
        List<CountDown> countDowns;
        try {
            countDowns = service.getAllCountDowns();
        } catch (Exception e) {
            Nlog.info("getAllCountDowns... err = " + e);
            return generateResult(-1, API.GET_COUNTDOWNS, "getAllCountDowns err", null);
        }
        return generateResult(0, API.GET_COUNTDOWNS, "success", countDowns);
    }

    @CrossOrigin
    @ResponseBody
    @PostMapping(value = API.DELETE_COUNTDOWN)
    public ResultPast deleteCountDown(@RequestBody String id) {
        try {
            // we get id like 1293812038019= ends with '=' who knows why ?
            if (id.endsWith("=")) {
                long idL = Long.parseLong(id.substring(0, id.length() - 1));
                service.deleteCountDownById(idL);
            } else {
                throw new IllegalArgumentException("Not ends with '='");
            }
        } catch (Exception e) {
            Nlog.info("deleteCountDown... err = " + e);
            return generateResult(-1, API.DELETE_COUNTDOWN, "deleteCountDown err", null);
        }
        return generateResult(0, API.DELETE_COUNTDOWN, "success", null);
    }

    private ResultPast generateResult(int code, String action, String msg, List<CountDown> countDowns) {
        ResultPast result = new ResultPast(code, msg);
        result.data = new CountDownData();
        ((CountDownData) (result.data)).action = action;
        ((CountDownData) (result.data)).countDowns = countDowns;
        if (code == 0) {
            Nlog.info("Successfully handle action = " + action);
        }
        return result;
    }

}
