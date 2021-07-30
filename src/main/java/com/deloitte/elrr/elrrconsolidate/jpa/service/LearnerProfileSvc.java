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
	private final LearnerProfileRepository learnerProfileRepository;

	public LearnerProfileSvc(final LearnerProfileRepository learnerProfileRepository) {
		this.learnerProfileRepository = learnerProfileRepository;
	}
	
	public LearnerProfile getLearnerProfileByPersonIdCourseId(long personId, long courseId) {
		return learnerProfileRepository.getLearnerProfileByPersonIdCourseId(personId, courseId);
	}

	@Override
	public CrudRepository<LearnerProfile, Long> getRepository() {
		return this.learnerProfileRepository;
	}

	@Override
	public Long getId(LearnerProfile learnerProfileFact) {
		return learnerProfileFact.getPersonid();
		}

	@Override
	public LearnerProfile save(LearnerProfile learnerProfileFact) {
		return CommonSvc.super.save(learnerProfileFact);
	}

}
