package org.adscale.loadtimer.service;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.ImmutableSet.copyOf;
import static com.google.common.util.concurrent.Futures.lazyTransform;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequest;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class AmazonSqsLoadTimeMessageSource implements LoadTimeMessageSource{

    private static final Function<? super Message, DeleteMessageBatchRequestEntry> GET_RECEIPT = new Function<Message, DeleteMessageBatchRequestEntry>() {
        @Override
        public DeleteMessageBatchRequestEntry apply(com.amazonaws.services.sqs.model.Message input) {
            return new DeleteMessageBatchRequestEntry().withId(input.getMessageId()).withReceiptHandle(input.getReceiptHandle());
        }
    };

    private final Function<Message,LoadTime> TO_MESSAGE = new Function<Message, LoadTime>() {
        private final ObjectMapper mapper = new ObjectMapper();
        @Override
        public LoadTime apply(Message input) {
            try {
                return mapper.reader(LoadTime.class).readValue(input.getBody());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private final Function<ReceiveMessageResult,Set<LoadTime>> TO_LOADTIMES = new Function<ReceiveMessageResult, Set<LoadTime>>() {
        @Override
        public Set<LoadTime> apply(ReceiveMessageResult input) {
            List<Message> messages = input.getMessages();
            client.deleteMessageBatchAsync(new DeleteMessageBatchRequest(queueUrl, ImmutableList.copyOf(transform(messages, GET_RECEIPT))));
            return copyOf(transform(messages, TO_MESSAGE));
        }
    };

    private final AmazonSQSAsync client;

    private final String queueUrl;

    private final ReceiveMessageRequest receiveRq;

    public AmazonSqsLoadTimeMessageSource(AmazonSQSAsync client, String queueUrl) {
        this.client = client;
        this.queueUrl = queueUrl;

        receiveRq = new ReceiveMessageRequest()
                .withMaxNumberOfMessages(1)
                .withQueueUrl(queueUrl);
    }


    @Override
    public Future<Set<LoadTime>> get() {
        Future<ReceiveMessageResult> receiveMessageResultFuture = client.receiveMessageAsync(receiveRq);
        return lazyTransform(receiveMessageResultFuture, TO_LOADTIMES);
    }
}
