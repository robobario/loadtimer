package org.adscale.loadtimer.service;

import static org.junit.Assert.assertEquals;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public class AmazonSqsLoadTimeMessageSourceTest {

    private static final String PAGE_ID = "adss";

    private static final String LOAD_TIME = "1233";


    @Test
    public void test() throws ExecutionException, InterruptedException {
        AmazonSQSAsync mock = Mockito.mock(AmazonSQSAsync.class);
        AmazonSqsLoadTimeMessageSource source = new AmazonSqsLoadTimeMessageSource(mock, "anyurl");
        Mockito.when(mock.receiveMessageAsync(Mockito.any(ReceiveMessageRequest.class))).thenReturn(Futures.immediateFuture(new ReceiveMessageResult().withMessages(new Message().withBody(
                "{\"pageIdentifier\":\"" + PAGE_ID + "\",\"loadTimeInSeconds\":" + LOAD_TIME + "}"))));
        Set<LoadTime> loadTimes = source.get().get();
        assertEquals(loadTimes, ImmutableSet.of(new LoadTime(PAGE_ID,Double.parseDouble(LOAD_TIME))));
    }

}
