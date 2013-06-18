package org.adscale.loadtimer.service.time;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeBucketKeyMaker {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd-HH-mm");


    public static String getHourlyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0);
        return print(bucket);
    }

    public static String getPreviousHourlyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).minusHours(1);
        return print(bucket);
    }

    public static String getNextHourlyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).plusHours(1);
        return print(bucket);
    }

    public static String getMonthlyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfMonth(1);
        return print(bucket);
    }

    public static String getDailyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0);
        return print(bucket);
    }


    public static String getWeeklyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0).withMinuteOfHour(0).withHourOfDay(0).withDayOfWeek(1);
        return print(bucket);
    }


    public static String getFiveMinutelyBucketKey(DateTime now) {
        DateTime bucket = now.withMillisOfSecond(0).withSecondOfMinute(0);
        bucket = bucket.withMinuteOfHour(bucket.getMinuteOfHour() - bucket.getMinuteOfHour() % 5);
        return print(bucket);
    }

    public static DateTime dateTimeForBucketKey(String key) {
       return DATE_TIME_FORMATTER.parseDateTime(key);
    }

    private static String print(DateTime bucket) {
        return DATE_TIME_FORMATTER.print(bucket);
    }


}
