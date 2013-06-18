package org.adscale.loadtimer.service.persistance;

import static com.google.common.collect.Iterators.getOnlyElement;
import static com.google.common.util.concurrent.Futures.lazyTransform;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getDailyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getFiveMinutelyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getHourlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getMonthlyBucketKey;
import static org.adscale.loadtimer.service.time.TimeBucketKeyMaker.getWeeklyBucketKey;

import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.internal.HttpFuture;
import com.couchbase.client.protocol.views.DesignDocument;
import com.couchbase.client.protocol.views.InvalidViewException;
import com.couchbase.client.protocol.views.View;
import com.couchbase.client.protocol.views.ViewResponse;
import com.couchbase.client.protocol.views.ViewRow;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;
import net.spy.memcached.internal.BulkFuture;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CouchbaseLoadTimeDao implements LoadTimeDao, InitializingBean{

    private static final Logger log = LoggerFactory.getLogger(CouchbaseLoadTimeDao.class);

    private final CouchbaseClient client;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final ObjectWriter WRITER = OBJECT_MAPPER.writer();

    private static final ObjectReader READER = OBJECT_MAPPER.reader(PageLoadHistory.class);

    private static final ObjectReader LOAD_AVERAGE_READER = OBJECT_MAPPER.reader(new TypeReference<List<LoadAverage>>(){});

    private static final Function<Map<String, Object>, Map<String, PageLoadHistory>> DESERIALIZE = new Function<Map<String, Object>, Map<String, PageLoadHistory>>() {
        @Override
        public Map<String, PageLoadHistory> apply(Map<String, Object> input) {
            ImmutableMap.Builder<String, PageLoadHistory> builder = ImmutableMap.builder();
            for (Map.Entry<String, Object> stringObjectEntry : input.entrySet()) {
                if(stringObjectEntry.getValue() != null){
                    try {
                        builder.put(stringObjectEntry.getKey(), READER.<PageLoadHistory>readValue((String) stringObjectEntry.getValue()));
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return builder.build();
        }
    };

    public CouchbaseLoadTimeDao(CouchbaseClient client) {
        this.client = client;
    }

    @Override
    public Future<Map<String, PageLoadHistory>> get(Set<String> identifiers) {
        BulkFuture<Map<String,Object>> bulkFuture = client.asyncGetBulk(identifiers);
        return lazyTransform(bulkFuture, DESERIALIZE);
    }

    @Override
    public Set<Future<Boolean>> putAll(Map<String, PageLoadHistory> historyMap) {
        ImmutableSet.Builder<Future<Boolean>> builder = ImmutableSet.builder();
        for (Map.Entry<String, PageLoadHistory> entry : historyMap.entrySet()) {
            try {
                builder.add(client.set(entry.getKey(), WRITER.writeValueAsString(entry.getValue())));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return builder.build();
    }


    @Override
    public List<LoadAverage> topTwentyLastFiveMinutes() throws IOException {
        String thisFiveMinuteKey = getFiveMinutelyBucketKey(DateTime.now());
        ViewResponse response = getLoadAveragesForViewByBucket(thisFiveMinuteKey, "highestFiveMinAvg");
        return transformSingleRowLoadAveragesResult(response);
    }


    @Override
    public List<LoadAverage> topTwentyLastHour(DateTime now) throws IOException {
        String thisHourKey = getHourlyBucketKey(now);
        ViewResponse response = getLoadAveragesForViewByBucket(thisHourKey, "highestHourly");
        return transformSingleRowLoadAveragesResult(response);
    }

    @Override
    public List<LoadAverage> topTwentyForHour(String bucketKey) throws IOException {
        ViewResponse response = getLoadAveragesForViewByBucket(bucketKey, "highestHourly");
        return transformSingleRowLoadAveragesResult(response);
    }



    @Override
    public List<LoadAverage> topTwentyLastDay() throws IOException {
        String currentDayKey = getDailyBucketKey(DateTime.now());
        ViewResponse response = getLoadAveragesForViewByBucket(currentDayKey, "highestDaily");
        return transformSingleRowLoadAveragesResult(response);
    }

    @Override
    public List<LoadAverage> topTwentyLastWeek() throws IOException {
        String currentWeekKey = getWeeklyBucketKey(DateTime.now());
        ViewResponse response = getLoadAveragesForViewByBucket(currentWeekKey, "highestWeekly");
        return transformSingleRowLoadAveragesResult(response);
    }

    @Override
    public List<LoadAverage> topTwentyLastMonth() throws IOException {
        String currentMonthKey = getMonthlyBucketKey(DateTime.now());
        ViewResponse response = getLoadAveragesForViewByBucket(currentMonthKey, "highestMonthly");
        return transformSingleRowLoadAveragesResult(response);
    }


    private List<LoadAverage> transformSingleRowLoadAveragesResult(ViewResponse response) throws IOException {
        if(response.size() == 0) {
            return ImmutableList.of();
        }
        ViewRow onlyElement = getOnlyElement(response.iterator());
        String value = onlyElement.getValue();
        return LOAD_AVERAGE_READER.readValue(value);
    }


    private ViewResponse getLoadAveragesForViewByBucket(String thisHourKey, String viewName) {
        View view = client.getView("loadtimerDesignDoc", viewName);
        com.couchbase.client.protocol.views.Query query = new com.couchbase.client.protocol.views.Query();
        query.setGroup(true);
        query.setKey(thisHourKey);
        return client.query(view, query);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        URL loadtimerDesignDoc = getClass().getClassLoader().getResource("loadtimerDesignDoc");
        String designDoc = Resources.toString(loadtimerDesignDoc, Charsets.UTF_8);
        try{
            DesignDocument current = client.getDesignDocument("loadtimerDesignDoc");
            ObjectReader reader = OBJECT_MAPPER.reader(JsonNode.class);
            if(!reader.readValue(current.toJson()).equals(reader.readValue(designDoc))){
                updateDesignDoc(designDoc);
            }
        }catch (InvalidViewException e){
            log.warn("no loadtimerDesignDoc found in couchbase cluster");
            updateDesignDoc(designDoc);
        }
    }


    private void updateDesignDoc(String newDesignDoc) throws UnsupportedEncodingException, InterruptedException, ExecutionException {
        HttpFuture<Boolean> doc = client.asyncCreateDesignDoc("loadtimerDesignDoc", newDesignDoc);
        log.info("design doc creation status " + (doc.get() ? "suceeded" : "failed " + doc.getStatus()));
    }
}
