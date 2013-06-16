package org.adscale.loadtimer.service;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LoadTime {

    private final String pageIdentifier;
    private final Double loadTimeInSeconds;

    @JsonCreator
    public LoadTime(@JsonProperty("pageIdentifier")String pageIdentifier, @JsonProperty("loadTimeInSeconds")Double loadTimeInSeconds) {
        this.pageIdentifier = pageIdentifier;
        this.loadTimeInSeconds = loadTimeInSeconds;
    }


    public Double getLoadTimeInSeconds() {
        return loadTimeInSeconds;
    }


    public String getPageIdentifier() {
        return pageIdentifier;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof LoadTime){
            final LoadTime other = (LoadTime) obj;
            return Objects.equal(pageIdentifier, other.pageIdentifier)
                    && loadTimeInSeconds.equals(other.loadTimeInSeconds);
        } else{
            return false;
        }
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(loadTimeInSeconds,pageIdentifier);
    }


    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("loadTimeInSeconds",loadTimeInSeconds).add("pageIdentifier",pageIdentifier).toString();
    }
}
