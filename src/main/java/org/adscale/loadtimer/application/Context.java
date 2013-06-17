package org.adscale.loadtimer.application;

import org.adscale.loadtimer.service.AmazonSqsLoadTimeMessageSource;
import org.adscale.loadtimer.service.LoadTimeMessageSink;
import org.adscale.loadtimer.service.LoadTimeMessageSource;
import org.adscale.loadtimer.service.LoadtimeSlurper;
import org.adscale.loadtimer.service.PersistingLoadTimeMessageSink;
import org.adscale.loadtimer.service.persistance.CouchbaseLoadTimeDao;
import org.adscale.loadtimer.service.persistance.LoadTimeDao;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.buffered.AmazonSQSBufferedAsyncClient;
import com.amazonaws.services.sqs.buffered.QueueBufferConfig;
import com.couchbase.client.CouchbaseClient;
import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;
import java.net.URI;

@Configuration
@EnableWebMvc
public class Context {

    @Value("${AWS_SECRET_KEY}")
    public String secretKey;

    @Value("${AWS_ACCESS_KEY_ID}")
    public String accessKey;

    @Value("${amazon.queue.url}")
    public String queueUrl;

    @Value("${couchbase.node.url}")
    public String couchbaseNode;

    @Bean
    public com.amazonaws.services.sqs.AmazonSQSAsyncClient asyncClient(){
        return new AmazonSQSAsyncClient(new BasicAWSCredentials(trimQuotes(accessKey),trimQuotes(secretKey)));
    }


    private String trimQuotes(String input) {
        if(input.startsWith("\"") && input.endsWith("\"") && input.length()>2){
            return input.substring(1, input.length() - 1);
        }
        return input;
    }


    @Bean
    public QueueBufferConfig queueBufferConfig(){
        return new QueueBufferConfig().withMaxBatchOpenMs(10000);
    }

    @Bean
    public AmazonSQSBufferedAsyncClient bufferedAsyncClient(){
        return new AmazonSQSBufferedAsyncClient(asyncClient(),queueBufferConfig());
    }

    @Bean
    public LoadTimeMessageSource getSource(){
        return new AmazonSqsLoadTimeMessageSource(bufferedAsyncClient(),queueUrl);
    }

    @Bean
    public LoadTimeDao getDao() throws IOException {
        URI uri = URI.create(couchbaseNode);
        ImmutableList<URI> nodes = ImmutableList.of(uri);
        CouchbaseClient client = new CouchbaseClient(nodes,"default","");
        return new CouchbaseLoadTimeDao(client);
    }

    @Bean
    public LoadTimeMessageSink getSink() throws IOException {
        return new PersistingLoadTimeMessageSink(getDao());
    }

    @Bean
    public LoadtimeSlurper client() throws IOException {
        return new LoadtimeSlurper(getSource(), getSink());
    }
}
