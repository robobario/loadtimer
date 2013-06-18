package org.adscale.loadtimer.controller;

import org.adscale.loadtimer.service.persistance.LoadAverage;

import java.util.List;

public class AverageResponse {

    private List<LoadAverage> loadAverages;

    private String bucketKey;

    private String nextKey = null;

    private String previousKey;

    public AverageResponse(List<LoadAverage> loadAverages, String thisBucketKey, String previousHourlyBucketKey, String nextHourlyBucketKey) {
        this.loadAverages = loadAverages;
        this.bucketKey = thisBucketKey;
        this.previousKey = previousHourlyBucketKey;
        this.nextKey = nextHourlyBucketKey;

    }

    public List<LoadAverage> getLoadAverages() {
        return loadAverages;
    }


    public String getBucketKey() {
        return bucketKey;
    }


    public String getNextKey() {
        return nextKey;
    }


    public String getPreviousKey() {
        return previousKey;
    }
}
