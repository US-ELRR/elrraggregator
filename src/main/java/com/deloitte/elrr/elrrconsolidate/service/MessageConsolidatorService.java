package com.deloitte.elrr.elrrconsolidate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.dto.CourseCompetency;
import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.entity.Competency;
import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;
import com.deloitte.elrr.elrrconsolidate.entity.Course;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageConsolidatorService {

	@Autowired
	HRService hrService;
	@Autowired
	ECCService eccService;
	
	@Autowired
	CASSService cassService;
	@Autowired
	ConsolidatorService consolidatorService;
	
	
	public void process(LearnerChange learnerChange) {
		
		ContactInformation contact = hrService.getContactInformation(learnerChange);
		List<Course> courses = eccService.getCourses(learnerChange);
		List<CourseCompetency> competencies = cassService.getCompetencies(learnerChange,courses);
		consolidateAndUpdate(contact,courses,competencies);
	}

	private void consolidateAndUpdate(ContactInformation contact, List<Course> courses,
			List<CourseCompetency> competencies) {
		consolidatorService.consolidate(contact, courses,competencies);
		
	}

}
