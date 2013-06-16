package org.adscale.loadtimer.service.persistance;

import static com.google.common.collect.ImmutableMap.of;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.HashMap;
import java.util.Map;

public class PageLoadHistory {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd-HH-mm");

    private final String identifier;

    private Map<String, LoadTimeStatistics> fiveMinutely = of();

    private Map<String, LoadTimeStatistics> hourly = of();

    private Map<String, LoadTimeStatistics> daily = of();

    private Map<String, LoadTimeStatistics> weekly = of();

    private Map<String, LoadTimeStatistics> monthly = of();


    public PageLoadHistory(String identifier) {
        this.identifier = identifier;
    }


    @JsonCreator
    public PageLoadHistory(@JsonProperty("identifier") String identifier, @JsonProperty("fiveMinutely") Map<String, LoadTimeStatistics> fiveMinutely,
                           @JsonProperty("hourly") Map<String, LoadTimeStatistics> hourly,
                           @JsonProperty("daily") Map<String, LoadTimeStatistics> daily,
                           @JsonProperty("weekly") Map<String, LoadTimeStatistics> weekly,
                           @JsonProperty("monthly") Map<String, LoadTimeStatistics> monthly) {
        this.identifier = identifier;
        this.fiveMinutely = fiveMinutely;
        this.hourly = hourly;
        this.daily = daily;
        this.weekly = weekly;
        this.monthly = monthly;
    }


    public void recordLoadTime(Double loadTime, DateTime eventTime) {
        updateFiveMinutely(loadTime, eventTime);
        updateHourly(loadTime, eventTime);
        updateDaily(loadTime, eventTime);
        updateWeekly(loadTime, eventTime);
        updateMonthly(loadTime, eventTime);
    }


    private void updateMonthly(Double loadTime, DateTime eventTime) {
        String bucketKey = getMonthlyBucketKey(eventTime);
        monthly = updateIntervalBuckets(loadTime, bucketKey, monthly);
    }


    private void updateWeekly(Double loadTime, DateTime eventTime) {
        String bucketKey = getWeeklyBucketKey(eventTime);
        weekly = updateIntervalBuckets(loadTime, bucketKey, weekly);
    }


    private void updateDaily(Double loadTime, DateTime eventTime) {
        String bucketKey = getDailyBucketKey(eventTime);
        daily = updateIntervalBuckets(loadTime, bucketKey, daily);
    }


    private void updateHourly(Double loadTime, DateTime now) {
        String bucketKey = getHourlyBucketKey(now);
        hourly = updateIntervalBuckets(loadTime, bucketKey, hourly);
    }


    private void updateFiveMinutely(Double loadTime, DateTime now) {
        String bucketKey = getFiveMinutelyBucketKey(now);
        fiveMinutely = updateIntervalBuckets(loadTime, bucketKey, fiveMinutely);
    }


    private ImmutableMap<String, LoadTimeStatistics> updateIntervalBuckets(Double loadTime, String bucketKey,
                                                                           Map<String, LoadTimeStatistics> bucket) {
        LoadTimeStatistics loadTimeStatistics = getExistingStatisticsOrCreateEmpty(bucketKey, bucket);
        loadTimeStatistics.addLoadTime(loadTime);
        HashMap<String, LoadTimeStatistics> temp = Maps.newHashMap();
        temp.putAll(bucket);
        temp.put(bucketKey, loadTimeStatistics);
        return ImmutableMap.copyOf(temp);
    }


    private LoadTimeStatistics getExistingStatisticsOrCreateEmpty(String bucketKey, Map<String, LoadTimeStatistics> bucket) {
        LoadTimeStatistics loadTimeStatistics = bucket.get(bucketKey);
        if (loadTimeStatistics == null) {
            loadTimeStatistics = new LoadTimeStatistics();
        }
        return loadTimeStatistics;
    }


    private String getHourlyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0);
        return DATE_TIME_FORMATTER.print(bucket);
    }


    private String getMonthlyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfMonth(1);
        return DATE_TIME_FORMATTER.print(bucket);
    }


    private String getDailyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0);
        return DATE_TIME_FORMATTER.print(bucket);
    }


    private String getWeeklyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfWeek(1);
        return DATE_TIME_FORMATTER.print(bucket);
    }


    private String getFiveMinutelyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0);
        bucket = bucket.withMinuteOfHour(bucket.getMinuteOfHour() - bucket.getMinuteOfHour() % 5);
        return DATE_TIME_FORMATTER.print(bucket);
    }


    public Map<String, LoadTimeStatistics> getFiveMinutely() {
        return fiveMinutely;
    }


    public Map<String, LoadTimeStatistics> getHourly() {
        return hourly;
    }


    public Map<String, LoadTimeStatistics> getDaily() {
        return daily;
    }


    public Map<String, LoadTimeStatistics> getWeekly() {
        return weekly;
    }


    public Map<String, LoadTimeStatistics> getMonthly() {
        return monthly;
    }


    public String getIdentifier() {
        return identifier;
    }

}
