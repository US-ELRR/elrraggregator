package com.deloitte.elrr.elrrconsolidate.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.dto.CourseCompetency;
import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.dto.UserCourse;
import com.deloitte.elrr.elrrconsolidate.entity.Competency;
import com.deloitte.elrr.elrrconsolidate.entity.Course;
import com.deloitte.elrr.elrrconsolidate.jpa.service.CompetencySvc;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CASSService {

	@Autowired
	CompetencySvc competencySvc;

	public List<CourseCompetency> getCompetencies(LearnerChange learnerChange, List<Course> courses) {
		log.info("Inside CASSService");
		List<String> competencyNames = invokeExternalService();
		List<Competency> competencies = getCompetencies(competencyNames);
		return getCourseCompetencies(courses, competencies, learnerChange);
	}

	private List<Competency> getCompetencies(List<String> competencyNames) {

		List<Competency> competencies = new ArrayList<>();
		for (String name : competencyNames) {
			log.info("querying Competencny for name " + name);
			Competency competency = competencySvc.findByCompetencyName(name);
			if (competency == null) {
				log.info("competency not found");
				// Where do we
				// createNewCompetency();
			} else {
				log.info("competency found " + competency.getCompetencyid());
			}
			competencies.add(competency);
		}

		return competencies;
	}

	private List<CourseCompetency> getCourseCompetencies(List<Course> courses, List<Competency> competencies,
			LearnerChange learnerChange) {

		List<CourseCompetency> courseCompetencies = new ArrayList<>();

		boolean first = true;
		for (Course course : courses) {
			CourseCompetency courseCompetency = new CourseCompetency();
			courseCompetency.setCourseId(course.getCourseid());
			if (first) {
				courseCompetency.setCompetencyId(competencies.get(0).getCompetencyid());
				first = false;
			} else {
				courseCompetency.setCompetencyId(competencies.get(1).getCompetencyid());
				first = true;
			}
			courseCompetency.setStatus(getStatus(course.getCourseidentifier(), learnerChange));
			courseCompetencies.add(courseCompetency);

		}
		return courseCompetencies;
	}

	private String getStatus(String courseidentifier, LearnerChange learnerChange) {
		String status = "Resumed";
		for (UserCourse course : learnerChange.getCourses()) {
			if (courseidentifier.equalsIgnoreCase(findCourseIdentifier(course.getCourseId()))) {
				log.info("sending status of " + course.getUserCourseStatus());
				// http://adlnet.gov/expapi/verbs/completed
				int begin = course.getUserCourseStatus().lastIndexOf("/");
				status = course.getUserCourseStatus().substring(begin + 1);
				return status;
			}
		}
		log.info("sending default status " + status);
		return status;
	}

	private String findCourseIdentifier(String courseId) {
		int lastIndex = courseId.lastIndexOf("/");
		String courseIdentier = courseId.substring(lastIndex + 1);
		courseIdentier = courseIdentier.replace("%20", " ");
		return courseIdentier;
	}

	private String getKey(String contactEmailAddress) {
		return contactEmailAddress.replace("mailto:", "");
	}

	/*
	 * Expectign this external service will provide all the information that will be
	 * requied to create a competency in the database if not found
	 * 
	 */
	private List<String> invokeExternalService() {

		List<String> competencies = new ArrayList<>();
		competencies.add("Skill and Roles: Business Skills and Acumen");
		competencies.add("Contract Principles: General Contracting Concepts");
		return competencies;
	}

}
