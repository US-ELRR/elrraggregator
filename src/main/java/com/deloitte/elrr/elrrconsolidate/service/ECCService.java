package com.deloitte.elrr.elrrconsolidate.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.dto.UserCourse;
import com.deloitte.elrr.elrrconsolidate.entity.Course;
import com.deloitte.elrr.elrrconsolidate.jpa.service.CourseSvc;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ECCService {

    /**
     *
     */
    @Autowired
    private CourseSvc courseService;

    /**
     *
     * @param learnerChange
     * @return List<Course> couseList
     */
    public List<Course> getCourses(final LearnerChange learnerChange) {
        log.info("Inside ECCService");
        List<Course> list = new ArrayList<>();
        for (UserCourse userCourse : learnerChange.getCourses()) {
            String courseIdentifier = findCourseIdentifier(
                    userCourse.getCourseId());
            log.info("courseIdentifier " + courseIdentifier);
            Course course = courseService
                    .getCourseByCourseidentifier(courseIdentifier);
            log.info("course " + course);
            if (course == null) {
                log.info("course is null");
                course = invokeExternalService();
            }
            list.add(course);
        }

        return list;
    }

    /**
     * Invokes external ECCService to get course details if course does not find
     * in the database.
     *
     * @return Course course
     */
    private Course invokeExternalService() {
        log.info("invoking externalService for Course");
        return null;
    }

    /*
     * gets the course Identifier
     */

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

}
