package com.deloitte.elrr.aggregator.rules;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Goal;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.GoalSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessRemoved implements Rule {

    @Autowired
    private GoalSvc goalService;

    /**
     * @param statement
     * @return boolean
     */
    public boolean fireRule(Statement statement) {

        // If not an activity
        if (!(statement.getObject() instanceof Activity)) {
            return false;
        }

        String objType = null;

        Activity obj = (Activity) statement.getObject();

        if (obj.getDefinition().getType() != null) {
            objType = obj.getDefinition().getType().toString();
        }

        // Is Verb Id = removed and object type = goal
        return (statement.getVerb().getId().toString().equalsIgnoreCase(
                VerbIdConstants.REMOVED_VERB_ID.toString())
                && ObjectTypeConstants.GOAL.equalsIgnoreCase(objType));
    }

    /**
     * @param person
     * @param statement
     * @return person
     * @throws AggregatorException
     * @throws URISyntaxException
     */
    public Person processRule(Person person, Statement statement)
            throws AggregatorException, ClassCastException,
            NullPointerException, RuntimeServiceException, URISyntaxException {

        log.info("Process removed.");

        // Get Activity
        Activity activity = (Activity) statement.getObject();

        // Get goal
        Goal goal = goalService.findByGoalId(activity.getId().toString());

        // Delete goal
        if (goal != null) {
            long rowsDeleted = goalService.deleteByGoalId(activity.getId()
                    .toString());
            log.info(rowsDeleted + " goals deleted.");
        }

        return person;

    }

}
