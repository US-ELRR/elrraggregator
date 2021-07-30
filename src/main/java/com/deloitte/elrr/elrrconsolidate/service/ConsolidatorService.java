package com.deloitte.elrr.elrrconsolidate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.dto.CourseCompetency;
import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;
import com.deloitte.elrr.elrrconsolidate.entity.Course;
import com.deloitte.elrr.elrrconsolidate.entity.LearnerProfile;
import com.deloitte.elrr.elrrconsolidate.jpa.service.LearnerProfileSvc;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConsolidatorService {

	@Autowired
	LearnerProfileSvc learnerProfileService;
	
	public void consolidate(ContactInformation contactInforation, List<Course> courses, List<CourseCompetency> competencies) {

		try {
		for (CourseCompetency courseCompetency: competencies) {
			LearnerProfile profile = learnerProfileService.getLearnerProfileByPersonIdCourseId(contactInforation.getPersonid(), courseCompetency.getCourseId());
			if (profile == null) {
				//create a new one
				profile = new LearnerProfile();
				log.info("courseCompetency.getCompetencyId() "+courseCompetency.getCompetencyId());
				profile.setPersonid(contactInforation.getPersonid());
				profile.setCompetencyid(courseCompetency.getCompetencyId());
				profile.setCourseid(courseCompetency.getCourseId());
				profile.setActivitystatus(courseCompetency.getStatus());
				//TODO remove hardcoded
				profile.setEmploymentid(100L);
				learnerProfileService.save(profile);
			} else {
				log.info("update courseCompetency.getCompetencyId() "+courseCompetency.getCompetencyId());
				log.info("update courseCompetency.getStatus() "+courseCompetency.getStatus());
				profile.setCompetencyid(courseCompetency.getCompetencyId());
				profile.setActivitystatus(courseCompetency.getStatus());
				
				learnerProfileService.save(profile);
			}
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
