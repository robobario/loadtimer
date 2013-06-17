package org.adscale.loadtimer.service.persistance;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

public interface LoadTimeDao {

    Future<Map<String, PageLoadHistory>> get(Set<String> identifiers);

    Set<Future<Boolean>> putAll(Map<String, PageLoadHistory> historyMap);

    List<LoadAverage> topTwentyLastFiveMinutes() throws IOException;
    List<LoadAverage> topTwentyLastHour() throws IOException;
    List<LoadAverage> topTwentyLastDay() throws IOException;
    List<LoadAverage> topTwentyLastWeek() throws IOException;
    List<LoadAverage> topTwentyLastMonth() throws IOException;

}
