package com.deloitte.elrr.aggregator.consumer;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.InputSanitizer;
import com.deloitte.elrr.aggregator.dto.MessageVO;
import com.deloitte.elrr.aggregator.rules.Rule;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.elrraggregator.exception.PersonNotFoundException;
import com.deloitte.elrr.entity.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ELRRMessageListener {

    @Autowired
    private Rule processCompleted;

    @Autowired
    private Rule processCompetency;

    @Autowired
    private Rule processCredential;

    @Autowired
    private ProcessPerson processPerson;

    @Autowired
    private Rule processPassed;

    @Autowired
    private Rule processFailed;

    @Autowired
    private Rule processInitialized;

    @Autowired
    private Rule processSatisfied;

    @Autowired
    private Rule processRegistered;

    @Autowired
    private Rule processScheduled;

    @Autowired
    private KafkaTemplate<?, String> kafkaTemplate;

    @Value("${kafka.dead.letter.topic}")
    private String deadLetterTopic;

    /**
     * @param message
     * @throws AggregatorException
     */
    @Transactional
    @KafkaListener(topics = "${kafka.topic}")
    public void listen(final String message) {

        log.info("\n\n Received Messasge in group - group-id== \n" + message);

        try {

            // If valid message process otherwise send to dead letter queue
            if (InputSanitizer.isValidInput(message)) {
                processMessage(message);
            } else {
                log.error("Invalid message did not pass whitelist check - "
                        + message);
                kafkaTemplate.send(deadLetterTopic, message);
            }

        } catch (AggregatorException e) {
            // Send to dead letter queue
            kafkaTemplate.send(deadLetterTopic, message);
            throw e;
        }
    }

    /**
     * @param payload
     * @throws AggregatorException
     */
    @Transactional
    public void processMessage(final String payload) {

        log.info(" \n\n ===============Process Kafka message===============");

        Statement statement = null;
        Person person = null;
        MessageVO messageVo = null;

        try {

            ObjectMapper mapper = Mapper.getMapper();

            // Get Statement
            messageVo = mapper.readValue(payload, MessageVO.class);
            statement = messageVo.getStatement();

            // Process Person
            person = processPerson.processPerson(statement);

            // *** ADD NEW RULES HERE ***
            List<Rule> classList = Arrays.asList(processCompetency,
                    processCompleted, processCredential, processFailed,
                    processInitialized, processPassed, processSatisfied,
                    processRegistered, processScheduled);

            for (Rule rule : classList) {

                log.info("Process verb " + statement.getVerb().getId() + " by "
                        + ruleToString(rule.toString()));

                if (rule.fireRule(statement)) {

                    // Process Rule
                    rule.processRule(person, statement);

                }
            }

        } catch (AggregatorException | PersonNotFoundException
                | JsonProcessingException e) {

            log.error("Error processing Kafka message", e);
            throw new AggregatorException("Error processing Kafka message.", e);

        }

    }

    /**
     * @param rule
     * @return String
     */
    private String ruleToString(String rule) {

        try {

            int indx = rule.indexOf("Process");
            int indx2 = rule.indexOf("@");
            rule = rule.substring(indx, indx2);

        } catch (StringIndexOutOfBoundsException e) {
            return rule;
        }

        return rule;
    }
}
