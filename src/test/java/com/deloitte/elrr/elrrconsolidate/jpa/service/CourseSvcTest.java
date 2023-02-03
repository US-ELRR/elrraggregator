package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.deloitte.elrr.elrrconsolidate.entity.Course;
import com.deloitte.elrr.elrrconsolidate.repository.CourseRepository;

/**
 *
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CourseSvcTest {

    /**
    *
    */
    @Mock
    private CourseRepository mockCourseRepository;

    /**
     *
     */
    @Test
    void test() {
        CourseSvc courseSvc = new CourseSvc(mockCourseRepository);
        Course course = new Course();
        course.setCourseid(1L);

        Mockito.doReturn(course).when(mockCourseRepository)
                .findIdByCourseidentifier("");
        Mockito.doReturn(course).when(mockCourseRepository).save(course);
        courseSvc.getCourseByCourseidentifier("");
        courseSvc.getId(course);
        courseSvc.get(1L);
        courseSvc.save(course);
    }

}
