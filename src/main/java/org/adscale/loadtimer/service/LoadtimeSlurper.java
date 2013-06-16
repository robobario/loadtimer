package org.adscale.loadtimer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadtimeSlurper {

    private final LoadTimeMessageSource source;
    private final LoadTimeMessageSink sink;

    private static final Logger log = LoggerFactory.getLogger(LoadtimeSlurper.class);

    ExecutorService service = Executors.newSingleThreadExecutor();

    public LoadtimeSlurper(final LoadTimeMessageSource source, LoadTimeMessageSink sink) {
        this.source = source;
        this.sink = sink;
        service.execute(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        Set<LoadTime> loadTimes = LoadtimeSlurper.this.source.get().get();
                        log.debug("processing {} loadtimes", new Object[]{loadTimes.size()});
                        LoadtimeSlurper.this.sink.slurp(loadTimes);
                    }
                    catch (Exception e) {
                        log.error("failed to eat",e);
                    }
                }
            }
        });
    }
}
