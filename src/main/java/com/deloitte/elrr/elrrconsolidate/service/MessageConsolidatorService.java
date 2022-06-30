package com.deloitte.elrr.elrrconsolidate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.dto.CourseCompetency;
import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;
import com.deloitte.elrr.elrrconsolidate.entity.Course;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageConsolidatorService {

    /**
     *
     */
    @Autowired
    private HRService hrService;

    /**
    *
    */
    @Autowired
    private ECCService eccService;

    /**
     *
     */
    @Autowired
    private CASSService cassService;

    /**
     *
     */
    @Autowired
    private ConsolidatorService consolidatorService;

    /**
     *
     * @param learnerChange
     */
    public void process(final LearnerChange learnerChange) {

        ContactInformation contact = hrService
                .getContactInformation(learnerChange);
        List<Course> courses = eccService.getCourses(learnerChange);
        List<CourseCompetency> competencies = cassService
                .getCompetencies(learnerChange, courses);
        consolidateAndUpdate(contact, competencies);
    }

    /**
     *
     * @param contact
     * @param competencies
     */
    private void consolidateAndUpdate(final ContactInformation contact,
            final List<CourseCompetency> competencies) {
        consolidatorService.consolidate(contact, competencies);

    }

}
