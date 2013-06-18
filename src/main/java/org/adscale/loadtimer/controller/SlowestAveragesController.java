package org.adscale.loadtimer.controller;

import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getHourlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getNextHourlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getPreviousHourlyBucketKey;

import org.adscale.loadtimer.service.persistance.LoadAverage;
import org.adscale.loadtimer.service.persistance.LoadTimeDao;

import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("slowest")
public class SlowestAveragesController {

    @Resource
    LoadTimeDao dao;


    @RequestMapping("last-five-minutes")
    public @ResponseBody List<LoadAverage> topTwentyLastFiveMinutes(HttpServletResponse response) {
        try {
            addAcal(response);
            return dao.topTwentyLastFiveMinutes();
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
    public @ResponseBody List<LoadAverage> topTwentyLastDay(HttpServletResponse response) {
        try {
            addAcal(response);
            return dao.topTwentyLastDay();
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }

    @RequestMapping("last-week")
    public @ResponseBody List<LoadAverage> topTwentyLastWeek(HttpServletResponse response) {
        try {
            addAcal(response);
            return dao.topTwentyLastWeek();
        }
        catch (IOException e) {
            throw new InternalServerErrorException(e);
        }
    }


    @RequestMapping("last-month")
    public @ResponseBody List<LoadAverage> topTwentyLastMonth(HttpServletResponse response) {
        try {
            addAcal(response);
            return dao.topTwentyLastMonth();
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
