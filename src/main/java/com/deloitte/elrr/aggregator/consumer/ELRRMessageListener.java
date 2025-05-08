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
import com.deloitte.elrr.exception.RuntimeServiceException;
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
    KafkaTemplate<?, String> kafkaTemplate;

    @Value("${kafka.dead.letter.topic}")
    private String deadLetterTopic;

    /**
     * @param message
     * @throws JsonProcessingException
     */
    @Transactional
    @KafkaListener(topics = "${kafka.topic}")
    public void listen(final String message) throws JsonProcessingException {

        log.info("\n\n ===============Received Messasge in group - group-id=============== \n" + message);

        try {

            // If valid message process otherwise send to dead letter queue
            if (InputSanitizer.isValidInput(message)) {
                processMessage(message);
            } else {
                String[] strings = { "Invalid message did not pass whitelist check -", message };
                log.error(String.join(" ", strings));
                kafkaTemplate.send(deadLetterTopic, message);
            }

        } catch (AggregatorException | JsonProcessingException e) {
            // Send to dead letter queue
            kafkaTemplate.send(deadLetterTopic, message);
            throw e;
        }
    }

    /**
     * @param statement
     * @throws JsonProcessingException
     * @throws AggregatorException
     */
    @Transactional
    public void processMessage(final String payload) throws JsonProcessingException {

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
            List<Rule> classList = Arrays.asList(processCompetency, processCompleted, processCredential, processFailed,
                    processInitialized, processPassed, processSatisfied, processRegistered, processScheduled);

            outerloop: for (Rule rule : classList) {

                String[] strings = { "Process verb", statement.getVerb().getId(), "by", ruleToString(rule.toString()) };
                log.info(String.join(" ", strings));

                if (rule.fireRule(statement)) {

                    // Process Rule
                    rule.processRule(person, statement);
                    break outerloop;

                }
            }

        } catch (AggregatorException | ClassCastException | NullPointerException | RuntimeServiceException
                | PersonNotFoundException | JsonProcessingException e) {

            String[] strings = { "Error processing Kafka message -", e.getMessage() };
            log.error(String.join(" ", strings));
            throw e;

        }

    }

    /**
     * @param rule
     * @return String
     */
    private String ruleToString(String rule) {
        int indx = rule.indexOf("Process");
        int indx2 = rule.indexOf("@");
        return rule.substring(indx, indx2);
    }
}
