package com.deloitte.elrr.elrrconsolidate.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.InputSanatizer;
import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.dto.MessageVO;
import com.deloitte.elrr.elrrconsolidate.dto.UserCourse;
import com.deloitte.elrr.elrrconsolidate.entity.ELRRAuditLog;
import com.deloitte.elrr.elrrconsolidate.jpa.service.ELRRAuditLogService;
import com.deloitte.elrr.elrrconsolidate.service.MessageConsolidatorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.ActivityDefinition;
import com.yetanalytics.xapi.model.Agent;
import com.yetanalytics.xapi.model.LangMap;
import com.yetanalytics.xapi.model.Statement;
import com.yetanalytics.xapi.model.Verb;
import com.yetanalytics.xapi.util.Mapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ELRRMessageListener {

    @Autowired
    private ELRRAuditLogService elrrAuditLogService;

    @Autowired
    private MessageConsolidatorService messageService;

    /**
     *
     * @param message
     */
    @KafkaListener(topics = "${kafka.topic}")
    public void listen(final String message) {
        if (InputSanatizer.isValidInput(message)) {
            log.info("Received Messasge in group - group-id: " + message);
            // PHL
            LearnerChange learnerChange = getLearnerChange(message);
            if (learnerChange != null) {
                messageService.process(learnerChange);
            }
        } else {
            log.warn("Invalid message did not pass whitelist check - "
                + message);
        }
    }

    /**
     * PHL
     * @param statement
     * @return LearnerChange
     */
    private LearnerChange getLearnerChange(final String payload) {
        ObjectMapper mapper = Mapper.getMapper(); 
        log.info("payload received " + payload);
        LearnerChange learnerChange = new LearnerChange();

        try {
            MessageVO messageVo = mapper.readValue(payload, MessageVO.class);
            insertAuditLog(messageVo, payload);
            Statement statement = messageVo.getStatement();
                           
            // Parse xAPI Statement
            // Actor
            String actorName = "";
            String actorEmail = "";
                
            Agent actor = (Agent) statement.getActor();
                
            if (actor != null) {
                actorName = actor.getName();
                actorEmail = actor.getMbox();
            }
                
            // Verb
            String verbDisplay = "";
                
            Verb verb = statement.getVerb();
                
            if (verb != null) {
                verbDisplay = verb.getDisplay().get("en-us");
            }
                      
            // Activity
            Activity object = (Activity) statement.getObject();
                
            // Activity name
            String activityName = "";
            String nameLangCode = "";
                
            ActivityDefinition activityDefenition = object.getDefinition();
            LangMap nameLangMap = activityDefenition.getName();
           
            if (nameLangMap != null) {
                Set<String> nameLangCodes = nameLangMap.getLanguageCodes();
                nameLangCode = nameLangCodes.iterator().next();
                activityName = activityDefenition.getName().get(nameLangCode);
            }
                         
            // Activity Description
            String activityDescription = "";
            String langCode = "";
                
            LangMap descLangMap = activityDefenition.getDescription();
                
            if (descLangMap != null) {
                Set<String> descLangCodes = descLangMap.getLanguageCodes();
                langCode = descLangCodes.iterator().next();
                activityDescription = activityDefenition.getDescription().get(langCode);
            }
                
            List<UserCourse> userCourses = new ArrayList<>();
                
            // Use xAPI Statement values
            learnerChange.setContactEmailAddress(actorEmail);
            learnerChange.setName(actorName);
            UserCourse course = new UserCourse();
            course.setCourseId(activityName);
            course.setCourseName(activityDescription);
            course.setUserCourseStatus(verbDisplay);
            userCourses.add(course);
            learnerChange.setCourses(userCourses);
                           
         } catch (JsonProcessingException e) {
            log.info("exception while inserting ");
            e.printStackTrace();
        }
        
        return learnerChange;
    }

    /**
     *
     * @param messageVo
     * @param payload
     */
    private void insertAuditLog(final MessageVO messageVo, final String payload) {
        ELRRAuditLog auditLog = new ELRRAuditLog();
        auditLog.setSyncid(messageVo.getAuditRecord().getAuditId());
        auditLog.setPayload(payload);
        elrrAuditLogService.save(auditLog);
    }

}
