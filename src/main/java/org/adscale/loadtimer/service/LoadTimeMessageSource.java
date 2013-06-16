package org.adscale.loadtimer.service;

import java.util.Set;
import java.util.concurrent.Future;

public interface LoadTimeMessageSource {

    Future<Set<LoadTime>> get();

}
