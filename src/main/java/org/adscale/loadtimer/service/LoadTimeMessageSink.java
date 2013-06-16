package org.adscale.loadtimer.service;

import java.util.Set;

public interface LoadTimeMessageSink {

    void slurp(Set<LoadTime> loadTime);
}
