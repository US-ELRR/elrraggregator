package com.deloitte.elrr.aggregator.consumer;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import lombok.extern.slf4j.Slf4j;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {

    @Value("${brokerUrl}")
    private String brokerUrl;

    @Value("${kafka.groupIdConfig}")
    private String groupIdConfig;

    /**
     * @return ConsumerFactory consumerFactory
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        log.info("Start building Kafka Consumer factory");
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdConfig);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        // Minimum 1MB data per fetch
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1048576");

        // Max 100MB per fetch
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "104857600");

        // Max 10MB per partition
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "10485760");

        // Poll 1000 records per poll
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "1000");

        // 1MB receive buffer
        props.put(ConsumerConfig.RECEIVE_BUFFER_CONFIG, "1048576");

        // 1MB send buffer
        props.put(ConsumerConfig.SEND_BUFFER_CONFIG, "1048576");

        // Manual commit of offsets
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // Session timeout
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "10000");

        // Start reading from latest
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * @return ConcurrentKafkaListenerContainerFactory
     *         concurrentKafkaListenerContainerFactory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
            kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory;
        factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(
                1000L, 2L)));
        return factory;
    }
}
