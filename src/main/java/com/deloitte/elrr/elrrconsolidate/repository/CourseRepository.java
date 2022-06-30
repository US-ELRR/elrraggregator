package com.deloitte.elrr.elrrconsolidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.deloitte.elrr.elrrconsolidate.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     *
     * @param courseidentifier
     * @return Course course
     */
    @Query("SELECT c FROM Course c WHERE LOWER(courseidentifier) "
            + "= LOWER(:courseidentifier) ")
    Course findIdByCourseidentifier(String courseidentifier);

}
