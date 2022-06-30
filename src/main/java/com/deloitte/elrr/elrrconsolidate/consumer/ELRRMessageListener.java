package com.deloitte.elrr.elrrconsolidate.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.dto.MessageVO;
import com.deloitte.elrr.elrrconsolidate.entity.ELRRAuditLog;
import com.deloitte.elrr.elrrconsolidate.jpa.service.ELRRAuditLogService;
import com.deloitte.elrr.elrrconsolidate.service.MessageConsolidatorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ELRRMessageListener {

    /**
     *
     */
    @Autowired
    private ELRRAuditLogService elrrAuditLogService;

    /**
     *
     */
    @Autowired
    private MessageConsolidatorService messageService;

    /**
     *
     * @param message
     */
    @KafkaListener(topics = "${kafka.topic}", groupId = "${kafka.groupId}")
    public void listen(final String message) {
        log.info("Received Messasge in group - group-id: " + message);
        LearnerChange learnerChange = getLearnerChange(message);
        messageService.process(learnerChange);
    }

    /**
     *
     * @param payload
     * @return LearnerChange learnerChange
     */
    private LearnerChange getLearnerChange(final String payload) {
        ObjectMapper mapper = new ObjectMapper();
        log.info("payload received " + payload);
        MessageVO messageVo = null;
        LearnerChange learner = null;
        try {
            messageVo = mapper.readValue(payload, MessageVO.class);
            insertAuditLog(messageVo, payload);
            learner = messageVo.getLearnerChange();
        } catch (JsonProcessingException e) {
            log.info("exception while inserting ");
            e.printStackTrace();
        }
        return learner;
    }

    /**
     *
     * @param messageVo
     * @param payload
     */
    private void insertAuditLog(final MessageVO messageVo,
            final String payload) {

        ELRRAuditLog auditLog = new ELRRAuditLog();
        auditLog.setSyncid(messageVo.getAuditRecord().getAuditId());
        auditLog.setPayload(payload);
        elrrAuditLogService.save(auditLog);
    }

}
