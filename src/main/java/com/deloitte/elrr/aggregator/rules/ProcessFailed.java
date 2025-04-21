package com.deloitte.elrr.aggregator.rules;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessFailed implements Rule {

  @Autowired LearningResourceUtil learningResourceUtil;

  @Autowired LearningRecordUtil learningRecordUtil;

  @Override
  public boolean fireRule(final Statement statement) {

    // Is Verb Id = failed and object = activity
    if (statement.getVerb().getId().equalsIgnoreCase(VerbIdConstants.FAILED_VERB_ID)
        && statement.getObject() instanceof Activity) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  @Transactional
  public Person processRule(final Person person, final Statement statement) {

    LearningResource learningResource = null;
    LearningRecord learningRecord = null;

    try {

      log.info("Process activity failed");

      // Get Activity
      Activity activity = (Activity) statement.getObject();

      // Process LearningResource
      learningResource = learningResourceUtil.processLearningResource(activity);

      // Process LearningRecord
      if (learningResource != null) {

        learningRecord =
            learningRecordUtil.processLearningRecord(
                activity, person, statement.getVerb(), statement.getResult(), learningResource);

        Set<LearningRecord> learningRecords = new HashSet<LearningRecord>();
        learningRecords.add(learningRecord);
        person.setLearningRecords(learningRecords);
        person.getLearningRecords().add(learningRecord);
      }

    } catch (AggregatorException
        | ClassCastException
        | NullPointerException
        | RuntimeServiceException e) {
      throw e;
    }

    return person;
  }
}
