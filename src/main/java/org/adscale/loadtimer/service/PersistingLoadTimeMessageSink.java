package org.adscale.loadtimer.service;

import static org.joda.time.DateTime.now;

import org.adscale.loadtimer.service.persistance.LoadTimeDao;
import org.adscale.loadtimer.service.persistance.PageLoadHistory;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.joda.time.DateTime;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class PersistingLoadTimeMessageSink implements LoadTimeMessageSink {

    private final LoadTimeDao dao;


    public PersistingLoadTimeMessageSink(LoadTimeDao dao) {
        this.dao = dao;
    }


    @Override
    public void slurp(Set<LoadTime> loadTime) {
        try {
            DateTime now = now();
            Set<String> keys = getKeys(loadTime);
            Map<String, PageLoadHistory> toPersist = getExistingHistories(keys);
            for (LoadTime time : loadTime) {
                PageLoadHistory pageLoadHistory = toPersist.get(time.getPageIdentifier());
                if (pageLoadHistory == null) {
                    pageLoadHistory = new PageLoadHistory(time.getPageIdentifier());
                    toPersist.put(time.getPageIdentifier(), pageLoadHistory);
                }
                pageLoadHistory.recordLoadTime(time.getLoadTimeInSeconds(), now);
            }
            dao.putAll(toPersist);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Set<String> getKeys(Set<LoadTime> loadTime) {
        Set<String> keys = Sets.newHashSet();
        for (LoadTime time : loadTime) {
           keys.add(time.getPageIdentifier());
        }
        return keys;
    }


    private Map<String, PageLoadHistory> getExistingHistories(Set<String> keys) throws InterruptedException, ExecutionException {
        Map<String,PageLoadHistory> toPersist = Maps.newHashMap();
        Map<String,PageLoadHistory> histories = dao.get(keys).get();
        toPersist.putAll(histories);
        return toPersist;
    }
}
