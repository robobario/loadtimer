package org.adscale.loadtimer.service.persistance;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

public interface LoadTimeDao {
    Future<Map<String, PageLoadHistory>> get(Set<String> identifiers);
    Set<Future<Boolean>> putAll(Map<String, PageLoadHistory> historyMap);
}
