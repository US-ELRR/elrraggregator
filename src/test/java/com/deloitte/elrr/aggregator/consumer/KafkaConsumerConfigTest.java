package com.deloitte.elrr.aggregator.consumer;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
class KafkaConsumerConfigTest {

    @Test
    void test() {

        KafkaConsumerConfig kafkaConsumerConfig = new KafkaConsumerConfig();
        assertNotNull(kafkaConsumerConfig);
    }
}
