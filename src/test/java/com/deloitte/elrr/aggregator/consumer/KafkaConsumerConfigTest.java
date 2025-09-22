package com.deloitte.elrr.aggregator.consumer;

import static org.assertj.core.api.Assertions.fail;
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

        try {

            KafkaConsumerConfig kafkaConsumerConfig = new KafkaConsumerConfig();

            ReflectionTestUtils.setField(kafkaConsumerConfig, "brokerUrl",
                    "elrr-kafka:9092");

            ReflectionTestUtils.setField(kafkaConsumerConfig, "groupIdConfig",
                    "elrr-Group");

            ReflectionTestUtils.setField(kafkaConsumerConfig, "fetchMinBytes",
                    "1048576");

            ReflectionTestUtils.setField(kafkaConsumerConfig, "fetchMaxBytes",
                    "104857600");

            ReflectionTestUtils.setField(kafkaConsumerConfig,
                    "maxPartitionFetchBytes", "10485760");

            ReflectionTestUtils.setField(kafkaConsumerConfig,
                    "maxPollingRecords", "1000");

            ReflectionTestUtils.setField(kafkaConsumerConfig, "receiveBuffer",
                    "1048576");

            ReflectionTestUtils.setField(kafkaConsumerConfig, "sendBuffer",
                    "1048576");

            ReflectionTestUtils.setField(kafkaConsumerConfig, "sessionTimeout",
                    "10000");

            ReflectionTestUtils.setField(kafkaConsumerConfig, "autoOffsetReset",
                    "latest");

            ConsumerFactory<String, String> factory = kafkaConsumerConfig
                    .consumerFactory();

            assertNotNull(kafkaConsumerConfig);
            assertNotNull(factory);

        } catch (NullPointerException e) {
            fail("Should not have thrown any exception");
        }

    }
}
