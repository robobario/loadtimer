package org.adscale.loadtimer.controller;

import org.adscale.loadtimer.service.persistance.LoadAverage;
import org.adscale.loadtimer.service.persistance.LoadTimeDao;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

@Controller
@RequestMapping("slowest")
public class SlowestAveragesController {

    @Resource
    LoadTimeDao dao;


    @RequestMapping("last-five-minutes")
    public @ResponseBody List<LoadAverage> topTwentyLastFiveMinutes() {
        try {
            return dao.topTwentyLastFiveMinutes();
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @RequestMapping("last-hour")
    public @ResponseBody List<LoadAverage> topTwentyLastHour() {
        try {
            return dao.topTwentyLastHour();
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @RequestMapping("last-day")
    public @ResponseBody List<LoadAverage> topTwentyLastDay() {
        try {
            return dao.topTwentyLastDay();
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @RequestMapping("last-week")
    public @ResponseBody List<LoadAverage> topTwentyLastWeek() {
        try {
            return dao.topTwentyLastWeek();
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }


    @RequestMapping("last-month")
    public @ResponseBody List<LoadAverage> topTwentyLastMonth() {
        try {
            return dao.topTwentyLastMonth();
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

}
