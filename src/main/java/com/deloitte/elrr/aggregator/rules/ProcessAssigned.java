package com.deloitte.elrr.aggregator.rules;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.consumer.ProcessPerson;
import com.deloitte.elrr.aggregator.utils.ExtensionsUtil;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Goal;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.GoalType;
import com.deloitte.elrr.jpa.svc.GoalSvc;
import com.yetanalytics.xapi.model.AbstractActor;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Context;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessAssigned implements Rule {

    @Autowired
    private ProcessPerson processPerson;

    @Autowired
    private LangMapUtil langMapUtil;

    @Autowired
    private GoalSvc goalService;

    @Autowired
    private LearningResourceUtil learningResourceUtil;

    @Autowired
    private ExtensionsUtil extensionsUtil;

    private static final String GOAL_MESSAGE = "Goal";

    /**
     * @param statement
     * @return boolean
     */
    @Override
    public boolean fireRule(final Statement statement) {

        // If not an activity
        if (!(statement.getObject() instanceof Activity)) {
            return false;
        }

        // Is Verb Id = assigned
        return (statement.getVerb().getId().toString().equalsIgnoreCase(
                VerbIdConstants.ASSIGNED_VERB_ID.toString()));

    }

    /**
     * @param person
     * @param statement
     * @return
     * @throws AggregatorException
     */
    @Override
    @Transactional
    public Person processRule(Person person, final Statement statement)
            throws AggregatorException {

        log.info("Process assigned");

        // Get start date
        // Convert from ZonedDateTime to LocalDate
        LocalDateTime startDate = statement.getTimestamp().toLocalDateTime();

        // Get Activity
        Activity activity = (Activity) statement.getObject();

        // Get assigned actor
        AbstractActor assignedActor = (AbstractActor) extensionsUtil
                .getExtensions(statement.getContext(), "Actor");

        // Get assigned person
        Person assignedPerson = processPerson.processPerson(assignedActor);

        // Process Goal
        Goal goal = processGoal(statement.getContext(), activity, startDate,
                assignedPerson);

        return person;
    }

    /**
     * @param context
     * @param activity
     * @param startDate
     * @param assignedPerson
     * @return goal
     * @throws AggregatorException
     */
    public Goal processGoal(final Context context, final Activity activity,
            final LocalDateTime startDate, final Person assignedPerson)
            throws AggregatorException {

        List<LearningResource> learningResources = new ArrayList<
                LearningResource>();
        Goal goal = null;

        // Process LearningResource
        learningResources = learningResourceUtil.processLearningResource(
                context);

        // Get goal
        // goal = goalService.findByIdentifier(activity.getId().toString());

        // If goal doesn't exist
        // if (goal == null) {

        goal = createGoal(activity, startDate, learningResources,
                assignedPerson);

        // } else {

        // log.info(GOAL_MESSAGE + " " + activity.getId() + " exists.");
        // goal = updateGoal(goal, activity);

        // }

        return goal;
    }

    /**
     * @param activity
     * @param startDate
     * @param learningResources
     * @param assignedActor
     * @return goal
     * @throws AggregatorException
     */
    public Goal createGoal(final Activity activity,
            final LocalDateTime startDate, final List<
                    LearningResource> learningResources,
            final Person assignedPerson) {

        log.info("Creating new goal.");

        GoalType goalType = null;
        Goal goal = null;
        String activityName = "";
        String activityDescription = "";

        activityName = langMapUtil.getLangMapValue(activity.getDefinition()
                .getName());
        activityDescription = langMapUtil.getLangMapValue(activity
                .getDefinition().getDescription());

        // Get goalType
        // if (activity != null && activity.getDefinition()
        // .getExtensions() != null) {

        // goalType = (GoalType) activity.getDefinition().getExtensions().get(
        // ExtensionsConstants.CONTEXT_EXTENSIONS_GOAL_TYPE);

        // }
        goalType = GoalType.ASSIGNED;

        goal = new Goal();
        goal.setDescription(activityDescription);
        goal.setName(activityName);
        goal.setType(goalType);
        goal.setLearningResources(new HashSet<>(learningResources));
        goal.setPerson(assignedPerson);
        goalService.save(goal);
        log.info(GOAL_MESSAGE + " " + activity.getId() + " created.");

        return goal;
    }

}
