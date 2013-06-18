package org.adscale.loadtimer.controller;

import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getDailyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getFiveMinutelyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getHourlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getMonthlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getNextHourlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getPreviousHourlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getWeeklyBucketKey;

import org.adscale.loadtimer.service.persistance.LoadTimeDao;
import org.adscale.loadtimer.service.time.TimeBucketKeyMaker;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("slowest")
public class SlowestAveragesController {

    @Resource
    LoadTimeDao dao;


    @RequestMapping("last-five-minutes")
    public @ResponseBody AverageResponse topTwentyLastFiveMinutes(HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime now = DateTime.now();
            String thisBucketKey = getFiveMinutelyBucketKey(now);
            String previousHourlyBucketKey = TimeBucketKeyMaker.getPreviousFiveMinutelyBucketKey(now);
            String nextHourlyBucketKey = TimeBucketKeyMaker.getNextFiveMinutelyBucketKey(now);
            return new AverageResponse(dao.topTwentyLastFiveMinutes(now), thisBucketKey, previousHourlyBucketKey, nextHourlyBucketKey);
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @RequestMapping("last-hour")
    public @ResponseBody AverageResponse topTwentyLastHour(HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime now = DateTime.now();
            String thisBucketKey = getHourlyBucketKey(now);
            String previousHourlyBucketKey = getPreviousHourlyBucketKey(now);
            String nextHourlyBucketKey = getNextHourlyBucketKey(now);
            return new AverageResponse(dao.topTwentyLastHour(now), thisBucketKey, previousHourlyBucketKey, nextHourlyBucketKey);
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @RequestMapping("last-day")
    public @ResponseBody AverageResponse topTwentyLastDay(HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime now = DateTime.now();
            String thisBucketKey = getDailyBucketKey(now);
            String previousHourlyBucketKey = TimeBucketKeyMaker.getPreviousDailyBucketKey(now);
            String nextHourlyBucketKey = TimeBucketKeyMaker.getNextDailyBucketKey(now);
            return new AverageResponse(dao.topTwentyLastDay(now), thisBucketKey, previousHourlyBucketKey, nextHourlyBucketKey);
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @RequestMapping("last-week")
    public @ResponseBody AverageResponse topTwentyLastWeek(HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime now = DateTime.now();
            String thisBucketKey = getWeeklyBucketKey(now);
            String previousBucketKey = TimeBucketKeyMaker.getPreviousWeeklyBucketKey(now);
            String nextBucketKey = TimeBucketKeyMaker.getNextWeeklyBucketKey(now);
            return new AverageResponse(dao.topTwentyLastWeek(now), thisBucketKey, previousBucketKey, nextBucketKey);
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }


    @RequestMapping("last-month")
    public @ResponseBody AverageResponse topTwentyLastMonth(HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime now = DateTime.now();
            String thisBucketKey = getMonthlyBucketKey(now);
            String previousBucketKey = TimeBucketKeyMaker.getPreviousMonthlyBucketKey(now);
            String nextBucketKey = TimeBucketKeyMaker.getNextMonthlyBucketKey(now);
            return new AverageResponse(dao.topTwentyLastMonth(now), thisBucketKey, previousBucketKey, nextBucketKey);
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
    public void commonOptions(HttpServletResponse theHttpServletResponse) throws IOException {
        theHttpServletResponse.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
        theHttpServletResponse.addHeader("Access-Control-Max-Age", "60"); // seconds to cache preflight request --> less OPTIONS traffic
        theHttpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        addAcal(theHttpServletResponse);
    }


    private void addAcal(HttpServletResponse theHttpServletResponse) {
        theHttpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
    }

}
