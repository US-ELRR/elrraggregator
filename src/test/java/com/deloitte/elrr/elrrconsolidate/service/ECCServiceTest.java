/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.dto.UserCourse;
import com.deloitte.elrr.elrrconsolidate.entity.Competency;
import com.deloitte.elrr.elrrconsolidate.jpa.service.CourseSvc;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ECCServiceTest {

    /**
    *
    */
    @Mock
    private CourseSvc courseService;

    @Test
    void test() {

        ECCService eCCService = new ECCService();

        LearnerChange learnerChange = new LearnerChange();
        learnerChange.setName("Test Course");
        learnerChange.setCourses(getUserCourseList());
        learnerChange.setContactEmailAddress("mailto:c.cooper@yahoo.com");

        Competency competency = new Competency();
        competency.setCompetencyid(1L);
        ReflectionTestUtils.setField(eCCService, "courseService",
                  courseService);

        eCCService.getCourses(learnerChange);
    }


    /**
     *
     * @return List<Course>
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
