/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.Course;
import com.deloitte.elrr.elrrconsolidate.repository.CourseRepository;

/**
 * @author mnelakurti
 *
 */

@Service
public class CourseSvc implements CommonSvc<Course, Long> {
    /**
     *
     */
    private final CourseRepository courseRepository;

    /**
     *
     * @param newcourseRepository
     */
    public CourseSvc(final CourseRepository newcourseRepository) {
        this.courseRepository = newcourseRepository;
    }

    /**
     *
     */
    @Override
    public CrudRepository<Course, Long> getRepository() {
        return this.courseRepository;
    }

    /**
     *
     */
    @Override
    public Long getId(final Course course) {
        return course.getCourseid();
    }

    /**
     *
     */
    @Override
    public Course save(final Course course) {
        return CommonSvc.super.save(course);
    }

    /**
     *
     * @param courseidentifier
     * @return Course
     */
    public Course getCourseByCourseidentifier(final String courseidentifier) {
        return this.courseRepository.findIdByCourseidentifier(courseidentifier);
    }

}
