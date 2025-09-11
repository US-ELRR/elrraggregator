package com.deloitte.elrr.aggregator.rules;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.deloitte.elrr.aggregator.utils.ExtensionsUtil;
import com.deloitte.elrr.aggregator.utils.LangMapUtil;
import com.deloitte.elrr.aggregator.utils.LearningResourceUtil;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.entity.Goal;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.types.GoalType;
import com.deloitte.elrr.exception.RuntimeServiceException;
import com.deloitte.elrr.jpa.svc.GoalSvc;
import com.yetanalytics.xapi.model.AbstractActor;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Context;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessWasAssigned implements Rule {

    @Autowired
    private LangMapUtil langMapUtil;

    @Autowired
    private GoalSvc goalService;

    @Autowired
    private LearningResourceUtil learningResourceUtil;

    @Autowired
    private ProcessCredential processCredential;

    @Autowired
    private ProcessCompetency processCompetency;

    @Autowired
    private ExtensionsUtil extensionsUtil;

    private static final String GOAL_MESSAGE = "Goal";

    /**
     * @param statement
     * @return boolean
     */
    public boolean fireRule(Statement statement) {

        // If not an activity
        if (!(statement.getObject() instanceof Activity)) {
            return false;
        }

        // Is Verb Id = assigned
        return (statement.getVerb().getId().toString().equalsIgnoreCase(
                VerbIdConstants.WAS_ASSIGNED_VERB_ID.toString()));
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

        log.info("Process was assigned.");

        GoalType goalType = null;

        // Get start date
        ZonedDateTime startDate = statement.getTimestamp();

        // Get Activity
        Activity activity = (Activity) statement.getObject();

        // Get assigning actor
        AbstractActor assigningActor = extensionsUtil.getExtensionValue(
                statement.getContext(),
                ExtensionsConstants.CONTEXT_EXTENSION_BY, AbstractActor.class);

        // If assigning actor present
        if (assigningActor != null) {
            goalType = GoalType.ASSIGNED;
        } else {
            goalType = GoalType.SELF;
        }

        // Process Goal
        processGoal(statement.getContext(), activity, person, startDate,
                goalType);

        return person;

    }

    /**
     * @param context
     * @param activity
     * @param person
     * @param startDate
     * @param goalType
     * @return goal
     * @throws AggregatorException
     * @throws URISyntaxException
     */
    @Transactional
    public Goal processGoal(final Context context, final Activity activity,
            final Person person, final ZonedDateTime startDate,
            final GoalType goalType) throws AggregatorException,
            URISyntaxException {

        // Get achieved by date
        ZonedDateTime achievedByDate = extensionsUtil.getExtensionsDate(
                activity, ExtensionsConstants.ACTIVITY_EXTENSION_ACHIEVED_BY);

        // Get activity expires
        ZonedDateTime endDate = extensionsUtil.getExtensionsDate(activity,
                ExtensionsConstants.ACTIVITY_EXTENSION_EXPIRES);

        // Process LearningResources
        List<LearningResource> learningResource = learningResourceUtil
                .processAssignedLearningResources(context);

        // Process Credentials
        List<Credential> credentials = (List<Credential>) processCredential
                .processAssignedCredentials(context);

        // Process Competencies
        List<Competency> competencies = (List<Competency>) processCompetency
                .processAssignedCompetencies(context);

        // Get goal
        Goal goal = goalService.findByGoalId(activity.getId().toString());

        // If goal doesn't exist
        if (goal == null) {

            goal = createGoal(activity, startDate, achievedByDate, endDate,
                    learningResource, credentials, competencies, person,
                    goalType);
            log.info(GOAL_MESSAGE + " " + goal.getName() + " created.");

            // If goal already exists
        } else {

            goal = updateGoal(goal, activity, endDate, achievedByDate);
            log.info(GOAL_MESSAGE + " " + goal.getName() + " updated.");

        }

        return goal;

    }

    /**
     * @param activity
     * @param startDate
     * @param achievedByDate
     * @param endDate
     * @param learningResources
     * @param credentials
     * @param competencies
     * @param person
     * @param goalType
     * @return goal
     * @throws AggregatorException
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public Goal createGoal(final Activity activity,
            final ZonedDateTime startDate, final ZonedDateTime achievedByDate,
            final ZonedDateTime endDate,
            final List<LearningResource> learningResources,
            final List<Credential> credentials,
            final List<Competency> competencies, final Person person,
            final GoalType goalType) throws AggregatorException {

        String activityName = langMapUtil.getLangMapValue(activity
                .getDefinition().getName());

        String activityDescription = langMapUtil.getLangMapValue(activity
                .getDefinition().getDescription());

        Goal goal = new Goal();
        goal.setGoalId(activity.getId().toString());
        goal.setDescription(activityDescription);
        goal.setName(activityName);
        goal.setType(goalType);
        goal.setStartDate(startDate);
        goal.setAchievedByDate(achievedByDate);
        goal.setExpirationDate(endDate);
        goal.setLearningResources(new HashSet<>(learningResources));
        goal.setCredentials(new HashSet<>(credentials));
        goal.setCompetencies(new HashSet<>(competencies));
        goal.setPerson(person);
        goalService.save(goal);

        return goal;

    }

    /**
     * @param goal
     * @param activity
     * @param achievedByDate
     * @param endDate
     * @return goal
     * @throws AggregatorException
     */
    public Goal updateGoal(Goal goal, Activity activity,
            final ZonedDateTime achievedByDate, final ZonedDateTime endDate)
            throws AggregatorException {

        GoalType goalType = null;

        String activityName = langMapUtil.getLangMapValue(activity
                .getDefinition().getName());

        String activityDescription = langMapUtil.getLangMapValue(activity
                .getDefinition().getDescription());

        // Get goalType
        if (activity != null && activity.getDefinition()
                .getExtensions() != null) {

            String type = (String) activity.getDefinition().getExtensions().get(
                    ExtensionsConstants.CONTEXT_EXTENSION_GOAL_TYPE);

            goalType = extensionsUtil.getGoalType(type);

        }

        goal.setDescription(activityDescription);
        goal.setName(activityName);
        goal.setType(goalType);
        goal.setAchievedByDate(achievedByDate);
        goal.setExpirationDate(endDate);
        goalService.update(goal);

        return goal;

    }

}
