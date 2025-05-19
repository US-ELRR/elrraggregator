package com.deloitte.elrr.aggregator.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.elrraggregator.exception.AggregatorException;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.yetanalytics.xapi.model.Activity;

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
                log.error("Error processing learning resource", e);
                e.printStackTrace();
                throw e;
            }
        }

        return learningResource;
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
            log.error(e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return learningResource;
    }
}
