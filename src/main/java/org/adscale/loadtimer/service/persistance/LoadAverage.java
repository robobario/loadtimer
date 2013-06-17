package org.adscale.loadtimer.service.persistance;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LoadAverage {

    private final String identifier;

    private final Double avg;


    @JsonCreator
    public LoadAverage(@JsonProperty("identifier") String identifier, @JsonProperty("avg") Double avg) {
        this.identifier = identifier;
        this.avg = avg;
    }


    public Double getAvg() {
        return avg;
    }


    public String getIdentifier() {
        return identifier;
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(avg, identifier);
    }


    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof LoadAverage) {
            final LoadAverage other = (LoadAverage) obj;
            return Objects.equal(avg, other.avg) && Objects.equal(identifier, other.identifier);
        }
        else {
            return false;
        }
    }
}
