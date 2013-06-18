package org.adscale.loadtimer.service.persistance;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

public interface LoadTimeDao {

    Future<Map<String, PageLoadHistory>> get(Set<String> identifiers);
    Set<Future<Boolean>> putAll(Map<String, PageLoadHistory> historyMap);

    List<LoadAverage> topTwentyLastFiveMinutes(DateTime now) throws IOException;
    List<LoadAverage> topTwentyLastHour(DateTime now) throws IOException;
    List<LoadAverage> topTwentyLastDay(DateTime now) throws IOException;
    List<LoadAverage> topTwentyLastWeek(DateTime now) throws IOException;
    List<LoadAverage> topTwentyLastMonth(DateTime now) throws IOException;


    List<LoadAverage> topTwentyForHour(String bucketKey) throws IOException;
    List<LoadAverage> topTwentyForDay(String bucketKey) throws IOException;
    List<LoadAverage> topTwentyForFiveMinute(String bucketKey) throws IOException;
    List<LoadAverage> topTwentyForWeek(String bucketKey) throws IOException;
    List<LoadAverage> topTwentyForMonth(String bucketKey) throws IOException;
}
