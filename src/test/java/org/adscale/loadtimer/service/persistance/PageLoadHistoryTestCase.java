package org.adscale.loadtimer.service.persistance;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class PageLoadHistoryTestCase {

    @Test
    public void testRecording(){
        PageLoadHistory id = new PageLoadHistory("id");
        DateTime now = new DateTime(2012, 1,2,5,7);
        id.recordLoadTime(5.0d, now);
        Map<String,LoadTimeStatistics> fiveMinutely = id.getFiveMinutely();
        assertEquals(fiveMinutely, ImmutableMap.of("2012-01-02-05-05", new LoadTimeStatistics(5.0d, 1L, 5.0d,6.0d)));
    }

    @Test
    public void testSerialization() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PageLoadHistory id = new PageLoadHistory("id");
        DateTime now = new DateTime(2012, 1,2,5,7);
        id.recordLoadTime(5.0d, now);
        String serialized = objectMapper.writer().writeValueAsString(id);
        PageLoadHistory p = objectMapper.reader(PageLoadHistory.class)
                .readValue(serialized);
        assertEquals(ImmutableMap.of("2012-01-02-05-05", new LoadTimeStatistics(5.0d, 1L, 5.0d,6.0d)), p.getFiveMinutely());
        assertEquals("id", p.getIdentifier());;
    }

}
