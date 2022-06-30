/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.LearnerProfile;
import com.deloitte.elrr.elrrconsolidate.repository.LearnerProfileRepository;

/**
 * @author mnelakurti
 *
 */

@Service
public class LearnerProfileSvc implements CommonSvc<LearnerProfile, Long> {

    /**
     *
     */
    private final LearnerProfileRepository learnerProfileRepository;

    /**
     *
     * @param newlearnerProfileRepository
     */
    public LearnerProfileSvc(final LearnerProfileRepository
            newlearnerProfileRepository) {
        this.learnerProfileRepository = newlearnerProfileRepository;
    }

    /**
     *
     * @param personId
     * @param courseId
     * @return LearnerProfile
     */
    public LearnerProfile getLearnerProfileByPersonIdCourseId(
            final long personId, final long courseId) {
        return learnerProfileRepository.
                getLearnerProfileByPersonIdCourseId(personId, courseId);
    }

    /**
     *
     */
    @Override
    public CrudRepository<LearnerProfile, Long> getRepository() {
        return this.learnerProfileRepository;
    }

    /**
     *
     */
    @Override
    public Long getId(final LearnerProfile learnerProfileFact) {
        return learnerProfileFact.getPersonid();
    }

    /**
     *
     */
    @Override
    public LearnerProfile save(final LearnerProfile learnerProfileFact) {
        return CommonSvc.super.save(learnerProfileFact);
    }

    public Object consolidate() {
        return null;
    }

}
