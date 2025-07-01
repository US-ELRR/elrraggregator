package com.deloitte.elrr.aggregator.consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.test.util.ReflectionTestUtils;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class KafkaConsumerConfigTest {

    @Test
    void test() {

        KafkaConsumerConfig kafkaConsumerConfig = new KafkaConsumerConfig();
        ReflectionTestUtils.setField(kafkaConsumerConfig, "brokerUrl", "localhost:9999");
        ReflectionTestUtils.setField(kafkaConsumerConfig, "groupIdConfig", "testGroup");
        ConsumerFactory<String, String> factory = kafkaConsumerConfig.consumerFactory();
        assertNotNull(kafkaConsumerConfig);
        assertNotNull(factory);
    }
}
