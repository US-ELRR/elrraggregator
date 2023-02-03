package com.deloitte.elrr.elrrconsolidate.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.dto.UserCourse;
import com.deloitte.elrr.elrrconsolidate.entity.Competency;
import com.deloitte.elrr.elrrconsolidate.entity.Course;
import com.deloitte.elrr.elrrconsolidate.jpa.service.CompetencySvc;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CASSServiceTest {

    /**
     *
     */
    @Mock
    private CompetencySvc mockcompetencySvc;

    /**
     *
     */
    @Test
    void testCASSService() {

        CASSService cASSService = new CASSService();

        LearnerChange learnerChange = new LearnerChange();
        learnerChange.setName("Test Course");
        learnerChange.setCourses(getUserCourseList());

        Competency competency = new Competency();
        competency.setCompetencyid(1L);

        ReflectionTestUtils.setField(cASSService, "competencySvc",
                mockcompetencySvc);
        Mockito.doReturn(competency).when(mockcompetencySvc)
                .findByCompetencyName(
                        "Skill and Roles: Business Skills and Acumen");

        cASSService.getCompetencies(learnerChange, getCourseList());

    }

    /**
    *
    */
    @Test
    void testCASSService2() {

        CASSService cASSService = new CASSService();

        LearnerChange learnerChange = new LearnerChange();
        learnerChange.setName("Test Course");
        learnerChange.setCourses(getUserCourseList());

        Competency competency = new Competency();
        competency.setCompetencyid(1L);

        ReflectionTestUtils.setField(cASSService, "competencySvc",
                mockcompetencySvc);
        Mockito.doReturn(competency).when(mockcompetencySvc)
                .findByCompetencyName(
                        "Skill and Roles: Business Skills and Acumen");
        Mockito.doReturn(competency).when(mockcompetencySvc)
                .findByCompetencyName(
                        "Contract Principles: General Contracting Concepts");
        cASSService.getCompetencies(learnerChange, getCourseList());

    }

    /**
     *
     */
    @Test
    void testCASSServiceWithoutCompetency() {

        CASSService cASSService = new CASSService();

        LearnerChange learnerChange = new LearnerChange();
        learnerChange.setName("Test Course");
        learnerChange.setCourses(getUserCourseList());

        Competency competency = new Competency();
        competency.setCompetencyid(1L);

        ReflectionTestUtils.setField(cASSService, "competencySvc",
                mockcompetencySvc);
        Mockito.doReturn(competency).when(mockcompetencySvc)
                .findByCompetencyName("");

        cASSService.getCompetencies(learnerChange, getCourseList());

    }

    /**
    *
    */
    @Test
    void testCASSServiceWithoutException() {

        CASSService cASSService = new CASSService();

        LearnerChange learnerChange = new LearnerChange();
        learnerChange.setName("");
        List<Course> courseList = new ArrayList<>();

        ReflectionTestUtils.setField(cASSService, "competencySvc",
                mockcompetencySvc);

        cASSService.getCompetencies(learnerChange, courseList);

    }

    /**
     *
     * @return List<Course>
     */
    private static List<Course> getCourseList() {
        List<Course> courseList = new ArrayList<>();
        Course course = new Course();
        course.setCourseid(1L);
        course.setCoursedescription("Test Course");
        course.setCourseidentifier("COURSEID");
        courseList.add(course);
        return courseList;
    }

    /**
     *
     * @return List<UserCourse>
     */
    private static List<UserCourse> getUserCourseList() {
        List<UserCourse> userCourseList = new ArrayList<>();
        UserCourse userCourse = new UserCourse();
        userCourse.setCourseId("COURSEID");
        userCourse.setCourseName("Test Course");
        userCourse.setUserCourseStatus(
                "http://adlnet.gov/expapi/verbs/completed");
        userCourseList.add(userCourse);
        return userCourseList;
    }
}
