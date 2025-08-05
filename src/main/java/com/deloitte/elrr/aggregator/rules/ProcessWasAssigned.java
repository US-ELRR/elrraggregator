package com.deloitte.elrr.aggregator.rules;

import java.net.URISyntaxException;
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
  private ProcessPerson processPerson;

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
      NullPointerException, RuntimeServiceException,
      URISyntaxException {

    log.info("Process was assigned.");

    // Get start date
    // Convert from ZonedDateTime to LocalDateTime
    LocalDateTime startDate = statement.getTimestamp().toLocalDateTime();

    // Get Activity
    Activity activity = (Activity) statement.getObject();

    // Get assigning actor
    AbstractActor assigningActor = (AbstractActor) extensionsUtil
        .getExtensions(statement.getContext(), "Actor");

    // If assigning actor present
    if (assigningActor != null) {

      // Process assigning person
      processPerson.processAssignedPerson(assigningActor);

    }

    // Process Goal
    processGoal(statement.getContext(), activity, person, startDate,
        person);

    return person;

  }

  /**
   * @param context
   * @param activity
   * @param person
   * @param startDate
   * @param assignedPerson
   * @return goal
   * @throws AggregatorException
   * @throws URISyntaxException
   */
  @Transactional
  public Goal processGoal(final Context context, final Activity activity,
      final Person person, final LocalDateTime startDate,
      final Person assignedPerson)
      throws AggregatorException, URISyntaxException {

    List<LearningResource> learnResources = new ArrayList<LearningResource>();
    List<Credential> credentials = new ArrayList<Credential>();
    List<Competency> competencies = new ArrayList<Competency>();
    Goal goal = null;
    LocalDateTime achievedByDate = null;
    LocalDateTime endDate = null;

    // Get achieved by date
    achievedByDate = extensionsUtil.getExtensionsDate(activity,
        ExtensionsConstants.CONTEXT_ACTIVITY_EXTENSIONS_ACHIEVED_BY);

    // Get activity expires
    endDate = extensionsUtil.getExtensionsDate(activity,
        ExtensionsConstants.CONTEXT_ACTIVITY_EXTENSIONS_EXPIRES);

    // Process LearningResources
    learnResources = learningResourceUtil.processAssignedLearningResources(
        context);

    // Process Credentials
    credentials = (List<Credential>) processCredential
        .processAssignedCredentials(
            context, person, startDate, endDate);

    // Process Competencies
    competencies = (List<Competency>) processCompetency
        .processAssignedCompetencies(
            context, person, startDate, endDate);

    // Get goal
    if (assignedPerson != null) {
      goal = goalService.findByPersonIdAndGoalId(assignedPerson.getId(),
          activity.getId().toString());
    }

    // If goal doesn't exist
    if (goal == null) {

      goal = createGoal(activity, startDate, achievedByDate, endDate,
          learnResources, credentials, competencies, assignedPerson);

      // If goal already exists
    } else {

      log.info(GOAL_MESSAGE + " " + activity.getId() + " exists.");
      goal = updateGoal(goal, activity, endDate, achievedByDate);

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
   * @param assignedPerson
   * @return goal
   * @throws AggregatorException
   */
  @SuppressWarnings("checkstyle:ParameterNumber")
  public Goal createGoal(final Activity activity,
      final LocalDateTime startDate, final LocalDateTime achievedByDate,
      final LocalDateTime endDate,
      final List<LearningResource> learningResources,
      final List<Credential> credentials, final List<Competency> competencies,
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
    if (activity != null && activity.getDefinition()
        .getExtensions() != null) {

      String type = (String) activity.getDefinition().getExtensions().get(
          ExtensionsConstants.CONTEXT_EXTENSIONS_GOAL_TYPE);

      goalType = extensionsUtil.getGoalType(type);

    }

    goal = new Goal();
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
    goal.setPerson(assignedPerson);
    goalService.save(goal);
    log.info(GOAL_MESSAGE + " " + activity.getId() + " created.");

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
      final LocalDateTime achievedByDate, final LocalDateTime endDate) {

    log.info("Updating goal.");

    GoalType goalType = null;
    String activityName = "";
    String activityDescription = "";

    activityName = langMapUtil.getLangMapValue(activity.getDefinition()
        .getName());
    activityDescription = langMapUtil.getLangMapValue(activity
        .getDefinition().getDescription());

    // Get goalType
    if (activity != null && activity.getDefinition()
        .getExtensions() != null) {

      String type = (String) activity.getDefinition().getExtensions().get(
          ExtensionsConstants.CONTEXT_EXTENSIONS_GOAL_TYPE);

      goalType = extensionsUtil.getGoalType(type);

    }

    goal.setDescription(activityDescription);
    goal.setName(activityName);
    goal.setType(goalType);
    goal.setAchievedByDate(achievedByDate);
    goal.setExpirationDate(endDate);
    goalService.update(goal);
    log.info(GOAL_MESSAGE + " " + activity.getId() + " updated.");

    return goal;

  }

}
