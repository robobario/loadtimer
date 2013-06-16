package org.adscale.loadtimer.service.persistance;

import com.google.common.base.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class LoadTimeStatistics {

    private Double avg = 0.0d;

    private Long count = 0L;

    private Double max = 0.0d;

    public LoadTimeStatistics() {
    }


    @JsonCreator
    public LoadTimeStatistics(@JsonProperty("avg") Double avg,@JsonProperty("count") Long count,@JsonProperty("max") Double max) {
        this.avg = avg;
        this.count = count;
        this.max = max;
    }


    public Double getMax() {
        return max;
    }


    public Long getCount() {
        return count;
    }


    public Double getAvg() {
        return avg;
    }


    public void addLoadTime(Double loadTime) {
        if (loadTime > max) {
            max = loadTime;
        }
        avg = ((avg * count) + loadTime)/(count + 1);
        count++;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(avg, count, max);
    }

    @Override
    public boolean equals(final Object obj){
        if(obj instanceof LoadTimeStatistics){
            final LoadTimeStatistics other = (LoadTimeStatistics) obj;
            return Objects.equal(avg, other.avg)
                    && Objects.equal(count, other.count)
                    && Objects.equal(max, other.max);
        } else{
            return false;
        }
    }
}
