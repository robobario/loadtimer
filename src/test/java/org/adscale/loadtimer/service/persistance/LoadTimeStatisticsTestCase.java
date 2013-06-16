package org.adscale.loadtimer.service.persistance;

import static java.lang.Long.valueOf;
import static org.junit.Assert.assertEquals;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

public class LoadTimeStatisticsTestCase {

    @Test
    public void testSerialization() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LoadTimeStatistics loadTimeStatistics = new LoadTimeStatistics(5.0d,5L, 5.0d);
        String serialized = objectMapper.writer().writeValueAsString(loadTimeStatistics);
        LoadTimeStatistics deSerialized = objectMapper.reader(LoadTimeStatistics.class).readValue(serialized);
        assertEquals(loadTimeStatistics,deSerialized);
    }

    @Test
    public void testAddingLoadTime(){
        LoadTimeStatistics loadTimeStatistics = new LoadTimeStatistics(5.0d,5L, 5.0d);
        loadTimeStatistics.addLoadTime(10.0d);
        assertEquals(valueOf(6L), loadTimeStatistics.getCount());
        assertEquals(Double.valueOf(10.0d), loadTimeStatistics.getMax());
        assertEquals(Double.valueOf(5.833333333333333d), loadTimeStatistics.getAvg());
    }


}
