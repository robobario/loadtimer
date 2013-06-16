package org.adscale.loadtimer.service.persistance;

import static com.google.common.util.concurrent.Futures.lazyTransform;

import com.couchbase.client.CouchbaseClient;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.spy.memcached.internal.BulkFuture;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

public class CouchbaseLoadTimeDao implements LoadTimeDao{

    private final CouchbaseClient client;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final ObjectWriter WRITER = OBJECT_MAPPER.writer();

    private static final ObjectReader READER = OBJECT_MAPPER.reader(PageLoadHistory.class);

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
}
