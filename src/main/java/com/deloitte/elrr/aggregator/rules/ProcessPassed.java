package com.deloitte.elrr.aggregator.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.LearningRecordUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessPassed implements Rule {
	
  @Autowired LearningResourceUtil learningResourceUtil;

  @Autowired LearningRecordUtil learningRecordUtil;

  @Override
  public boolean fireRule(final Statement statement) {

    // Is Verb Id = passed and object = activity
    if (statement.getVerb().getId().equalsIgnoreCase(VerbIdConstants.PASSED_VERB_ID)
        && statement.getObject() instanceof Activity) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  @Transactional
  public void processRule(final Person person, final Statement statement) {

    try {

      log.info("Process activity passed");

      // Get Activity
      Activity activity = (Activity) statement.getObject();

      // Process LearningResource
      LearningResource learningResource = learningResourceUtil.processLearningResource(activity);

      // Process LearningRecord
      if (learningResource != null) {
        learningRecordUtil.processLearningRecord(
            activity, person, statement.getVerb(), statement.getResult(), learningResource);
      }

    } catch (AggregatorException
        | ClassCastException
        | NullPointerException
        | RuntimeServiceException e) {
      throw e;
    }
  }
}
