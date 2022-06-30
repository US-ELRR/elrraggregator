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

    /**
     *
     */
    @Autowired
    private CompetencySvc competencySvc;

    /**
     *
     * @param learnerChange
     * @param courses
     * @return List<CourseCompetency> CourseCompetencies
     */
    public List<CourseCompetency> getCompetencies(
            final LearnerChange learnerChange, final List<Course> courses) {
        log.info("Inside CASSService");
        List<String> competencyNames = invokeExternalService();
        List<Competency> competencies = getCompetencies(competencyNames);
        return getCourseCompetencies(courses, competencies, learnerChange);
    }

    /**
     *
     * @param competencyNames
     * @return List<Competency> competencies
     */
    private List<Competency> getCompetencies(
            final List<String> competencyNames) {

        List<Competency> competencies = new ArrayList<>();
        for (String name : competencyNames) {
            log.info("querying Competencny for name " + name);
            Competency competency = competencySvc.findByCompetencyName(name);
            if (competency == null) {
                log.info("competency not found");
            } else {
                log.info("competency found " + competency.getCompetencyid());
            }
            competencies.add(competency);
        }

        return competencies;
    }

    /**
     *
     * @param courses
     * @param competencies
     * @param learnerChange
     * @return List<CourseCompetency> courseCompetencies
     */
    private List<CourseCompetency> getCourseCompetencies(
            final List<Course> courses, final List<Competency> competencies,
            final LearnerChange learnerChange) {

        List<CourseCompetency> courseCompetencies = new ArrayList<>();

        boolean first = true;
        for (Course course : courses) {
            CourseCompetency courseCompetency = new CourseCompetency();
            courseCompetency.setCourseId(course.getCourseid());
            if (first) {
                courseCompetency
                        .setCompetencyId(competencies.get(0).getCompetencyid());
                first = false;
            } else {
                courseCompetency
                        .setCompetencyId(competencies.get(1).getCompetencyid());
                first = true;
            }
            courseCompetency.setStatus(
                    getStatus(course.getCourseidentifier(), learnerChange));
            courseCompetencies.add(courseCompetency);

        }
        return courseCompetencies;
    }

    /**
     *
     * @param courseidentifier
     * @param learnerChange
     * @return String status
     */
    private String getStatus(final String courseidentifier,
            final LearnerChange learnerChange) {
        String status = "Resumed";
        for (UserCourse course : learnerChange.getCourses()) {
            if (courseidentifier.equalsIgnoreCase(
                    findCourseIdentifier(course.getCourseId()))) {
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

    /**
     *
     * @param courseId
     * @return String courseIdentier
     */
    private String findCourseIdentifier(final String courseId) {
        int lastIndex = courseId.lastIndexOf("/");
        String courseIdentier = courseId.substring(lastIndex + 1);
        courseIdentier = courseIdentier.replace("%20", " ");
        return courseIdentier;
    }

    /**
     * Expecting this external service will provide all the information that
     * will be required to create a competency in the database if not found.
     *
     * @return List competencies
     */
    private List<String> invokeExternalService() {

        List<String> competencies = new ArrayList<>();
        competencies.add("Skill and Roles: Business Skills and Acumen");
        competencies.add("Contract Principles: General Contracting Concepts");
        return competencies;
    }

}
