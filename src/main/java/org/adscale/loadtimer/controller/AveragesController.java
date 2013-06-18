package org.adscale.loadtimer.controller;

import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getNextHourlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getPreviousHourlyBucketKey;

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
