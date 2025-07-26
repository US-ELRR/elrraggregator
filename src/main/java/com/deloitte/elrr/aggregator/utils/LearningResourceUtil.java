package com.deloitte.elrr.aggregator.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public LearningResource processLearningResource(final Activity activity) {

        log.info("Process learning resource.");

        // Get learningResource
        LearningResource learningResource = learningResourceService.findByIri(
                activity.getId().toString());

        // If LearningResource already exists
        if (learningResource != null) {

            log.info("Learning Resource " + learningResource.getTitle()
                    + " exists.");

        } else {

            try {

                learningResource = createLearningResource(activity);

            } catch (AggregatorException e) {
                throw e;
            }
        }

        return learningResource;
    }

    /**
     * @param context
     * @return learningResources
     * @throws AggregatorException
     */
    public List<LearningResource> processLearningResource(
            final Context context) {

        log.info("Process learning resources.");

        List<LearningResource> learningResources = new ArrayList<
                LearningResource>();

        // Get learningResources
        List<Activity> activities = context.getContextActivities().getOther();

        for (Activity activity : activities) {

            LearningResource learningResource = learningResourceService
                    .findByIri(activity.getId().toString());

            // If LearningResource already exists
            if (learningResource != null) {

                log.info("Learning Resource " + learningResource.getTitle()
                        + " exists.");

            } else {

                try {

                    learningResource = createLearningResource(activity);
                    learningResources.add(learningResource);

                } catch (AggregatorException e) {
                    throw e;
                }
            }

        }

        return learningResources;
    }

    /**
     * @param activity
     * @return learningResource
     * @throws AggregatorException
     */
    private LearningResource createLearningResource(final Activity activity) {

        log.info("Creating new learning resource.");

        LearningResource learningResource = null;

        try {

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

        } catch (AggregatorException e) {
            throw e;
        }

        return learningResource;
    }
}
