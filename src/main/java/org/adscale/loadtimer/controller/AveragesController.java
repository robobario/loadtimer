package org.adscale.loadtimer.controller;

import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getNextDailyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getNextFiveMinutelyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getNextHourlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getNextMonthlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getNextWeeklyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getPreviousDailyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getPreviousFiveMinutelyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getPreviousHourlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getPreviousMonthlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getPreviousWeeklyBucketKey;

import org.adscale.loadtimer.service.persistance.LoadTimeDao;
import org.adscale.loadtimer.service.time.TimeBucketKeyMaker;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("averages")
public class AveragesController {

    @Resource
    LoadTimeDao dao;

    @RequestMapping("/fiveMinute/{bucketKey}")
    public @ResponseBody
    AverageResponse topTwentyForFiveMinute(@PathVariable String bucketKey,HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime dateTime = TimeBucketKeyMaker.dateTimeForBucketKey(bucketKey);
            String previousBucketKey = getPreviousFiveMinutelyBucketKey(dateTime);
            String nextBucketKey = getNextFiveMinutelyBucketKey(dateTime);
            return new AverageResponse(dao.topTwentyForFiveMinute(bucketKey), bucketKey, previousBucketKey, nextBucketKey);
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }


    @RequestMapping("/hour/{bucketKey}")
    public @ResponseBody
    AverageResponse topTwentyForHour(@PathVariable String bucketKey,HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime dateTime = TimeBucketKeyMaker.dateTimeForBucketKey(bucketKey);
            String previousHourlyBucketKey = getPreviousHourlyBucketKey(dateTime);
            String nextHourlyBucketKey = getNextHourlyBucketKey(dateTime);
            return new AverageResponse(dao.topTwentyForHour(bucketKey), bucketKey, previousHourlyBucketKey, nextHourlyBucketKey);
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @RequestMapping("/day/{bucketKey}")
    public @ResponseBody
    AverageResponse topTwentyForDay(@PathVariable String bucketKey,HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime dateTime = TimeBucketKeyMaker.dateTimeForBucketKey(bucketKey);
            String previousBucketKey = getPreviousDailyBucketKey(dateTime);
            String nextBucketKey = getNextDailyBucketKey(dateTime);
            return new AverageResponse(dao.topTwentyForDay(bucketKey), bucketKey, previousBucketKey, nextBucketKey);
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }


    @RequestMapping("/week/{bucketKey}")
    public @ResponseBody
    AverageResponse topTwentyForWeek(@PathVariable String bucketKey,HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime dateTime = TimeBucketKeyMaker.dateTimeForBucketKey(bucketKey);
            String previousBucketKey = getPreviousWeeklyBucketKey(dateTime);
            String nextBucketKey = getNextWeeklyBucketKey(dateTime);
            return new AverageResponse(dao.topTwentyForWeek(bucketKey), bucketKey, previousBucketKey, nextBucketKey);
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }


    @RequestMapping("/month/{bucketKey}")
    public @ResponseBody
    AverageResponse topTwentyForMonth(@PathVariable String bucketKey,HttpServletResponse response) {
        try {
            addAcal(response);
            DateTime dateTime = TimeBucketKeyMaker.dateTimeForBucketKey(bucketKey);
            String previousBucketKey = getPreviousMonthlyBucketKey(dateTime);
            String nextBucketKey = getNextMonthlyBucketKey(dateTime);
            return new AverageResponse(dao.topTwentyForMonth(bucketKey), bucketKey, previousBucketKey, nextBucketKey);
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
