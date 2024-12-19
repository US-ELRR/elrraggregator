package com.deloitte.elrr.elrrconsolidate.service;

import java.nio.charset.StandardCharsets;
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
            String courseIdentifier = findCourseIdentifier(userCourse.getCourseId());
            log.info("courseIdentifier " + courseIdentifier);
            Course course = courseService.getCourseByCourseidentifier(courseIdentifier);
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
     *
     * @param courseId
     * @return String courseIdentier
    */
   private String findCourseIdentifier(final String courseId) {
      // Remove https://w3id.org/xapi/credential/ 
      int lastIndex = courseId.lastIndexOf("/");
      String courseIdentifier = courseId.substring(lastIndex + 1);
      
      // Decode URL encoding
      courseIdentifier = java.net.URLDecoder.decode(courseIdentifier, StandardCharsets.UTF_8);
      
      return courseIdentifier;
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

}
