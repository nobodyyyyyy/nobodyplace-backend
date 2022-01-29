package com.nobody.nobodyplace.controller;

import com.nobody.nobodyplace.entity.CountDown;
import com.nobody.nobodyplace.response.CountDownData;
import com.nobody.nobodyplace.response.Result;
import com.nobody.nobodyplace.service.CountDownService;
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
    public Result addCountDown(@RequestBody CountDown countDown) {
        return addOrUpdateCountDown(API.ADD_COUNTDOWN, countDown);
    }

    @CrossOrigin
    @PostMapping(value = API.UPDATE_COUNTDOWN)
    @ResponseBody
    public Result updateCountDown(@RequestBody CountDown countDown) {
        return addOrUpdateCountDown(API.UPDATE_COUNTDOWN, countDown);
    }

    private Result addOrUpdateCountDown(String action, CountDown countDown) {
        try {
            service.addOrUpdate(countDown);
        } catch (Exception e) {
            Nlog.info("addOrUpdateCountDown... action = " + action + ", err = " + e);
            return generateResult(404, action, "addOrUpdateCountDown err", null);
        }
        return generateResult(200, action, "success", null);
    }

    @CrossOrigin
    @ResponseBody
    @GetMapping(value = API.GET_COUNTDOWNS)
    public Result getAllCountDowns() {
        List<CountDown> countDowns;
        try {
            countDowns = service.getAllCountDowns();
        } catch (Exception e) {
            Nlog.info("getAllCountDowns... err = " + e);
            return generateResult(404, API.GET_COUNTDOWNS, "getAllCountDowns err", null);
        }
        return generateResult(200, API.GET_COUNTDOWNS, "success", countDowns);
    }

    @CrossOrigin
    @ResponseBody
    @PostMapping(value = API.DELETE_COUNTDOWN)
    public Result deleteCountDown(@RequestBody String id) {
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
            return generateResult(404, API.DELETE_COUNTDOWN, "deleteCountDown err", null);
        }
        return generateResult(200, API.DELETE_COUNTDOWN, "success", null);
    }

    private Result generateResult(int code, String action, String msg, List<CountDown> countDowns) {
        Result result = new Result(code, msg);
        result.data = new CountDownData();
        ((CountDownData) (result.data)).action = action;
        ((CountDownData) (result.data)).countDowns = countDowns;
        if (code == 200) {
            Nlog.info("Successfully handle action = " + action);
        }
        return result;
    }

}
