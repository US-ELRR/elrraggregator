package com.deloitte.elrr.aggregator.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.aggregator.rules.ContextActivitiesTypeConstants;
import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.yetanalytics.xapi.model.Activity;
import com.yetanalytics.xapi.model.Context;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LearningResourceUtil {

    @Autowired
    private LearningResourceSvc learningResourceService;

    @Autowired
    private LangMapUtil langMapUtil;

    /**
     * @param activity
     * @return learningResource
     * @throws AggregatorException
     */
    public LearningResource processLearningResource(final Activity activity)
            throws AggregatorException {

        log.info("Process learning resource.");

        // Get learningResource
        LearningResource learningResource = learningResourceService.findByIri(
                activity.getId().toString());

        // If LearningResource already exists
        if (learningResource != null) {

            log.info("Learning Resource " + learningResource.getTitle()
                    + " exists.");

        } else {

            learningResource = createLearningResource(activity);

        }

        return learningResource;
    }

    /**
     * @param context
     * @return learningResources
     * @throws URISyntaxException
     * @throws AggregatorException
     */
    public List<LearningResource> processAssignedLearningResources(
            final Context context) throws URISyntaxException,
            AggregatorException {

        log.info("Process learning resources.");

        List<LearningResource> lrnRes = new ArrayList<LearningResource>();

        // Get learningResources
        List<Activity> activities = context.getContextActivities().getOther();

        for (Activity activity : activities) {

            // Get type
            URI type = activity.getDefinition().getType();

            // Not a LearningResource if Credential or Competency
            if (type.equals(ContextActivitiesTypeConstants.OTHER_CRED_URI)
                    || type.equals(
                            ContextActivitiesTypeConstants.OTHER_COMP_URI)) {
                return lrnRes;
            }

            LearningResource learningResource = learningResourceService
                    .findByIri(activity.getId().toString());

            // If LearningResource already exists
            if (learningResource != null) {

                log.info("Learning Resource " + learningResource.getTitle()
                        + " exists.");

            } else {

                learningResource = createLearningResource(activity);
                lrnRes.add(learningResource);

            }

        }

        return lrnRes;

    }

    /**
     * @param activity
     * @return learningResource
     * @throws AggregatorException
     */
    private LearningResource createLearningResource(final Activity activity)
            throws AggregatorException {

        LearningResource learningResource = null;

        String activityName = langMapUtil.getLangMapValue(activity
                .getDefinition().getName());
        String activityDescription = langMapUtil.getLangMapValue(activity
                .getDefinition().getDescription());

        learningResource = new LearningResource();
        learningResource.setIri(activity.getId().toString());
        learningResource.setDescription(activityDescription);
        learningResource.setTitle(activityName);
        learningResourceService.save(learningResource);

        log.info("Learning Resource " + learningResource.getTitle()
                + " created.");

        return learningResource;

    }

}
