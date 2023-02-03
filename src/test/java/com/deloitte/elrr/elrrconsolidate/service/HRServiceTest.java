/**
 *
 */
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
import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;
import com.deloitte.elrr.elrrconsolidate.jpa.service.ContactInformationSvc;
import com.deloitte.elrr.elrrconsolidate.jpa.service.PersonSvc;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HRServiceTest {

    /**
    *
    */
    @Mock
    private PersonSvc personService;

    /**
    *
    */
    @Mock
    private ContactInformationSvc contactInformationService;

    /**
     * Test method for
     * {@link com.deloitte.elrr.elrrconsolidate.service.
     * HRService#getContactInformation
     * (com.deloitte.elrr.elrrconsolidate.dto.LearnerChange)}.
     */
    @Test
    void testGetContactInformation() {
        HRService hrService = new HRService();

        LearnerChange learnerChange = new LearnerChange();
        learnerChange.setName("Test Course");
        learnerChange.setCourses(getUserCourseList());
        learnerChange.setContactEmailAddress("mailto:c.cooper@yahoo.com");

        Competency competency = new Competency();
        competency.setCompetencyid(1L);

        ReflectionTestUtils.setField(hrService, "personService", personService);
        ReflectionTestUtils.setField(hrService, "contactInformationService",
                contactInformationService);
        hrService.getContactInformation(learnerChange);
    }

    /**
     * Test method for
     * {@link com.deloitte.elrr.elrrconsolidate.service.
     * HRService#getContactInformation
     * (com.deloitte.elrr.elrrconsolidate.dto.LearnerChange)}.
     */
    @Test
    void testGetContactInformationWithData() {
        HRService hrService = new HRService();

        LearnerChange learnerChange = new LearnerChange();
        learnerChange.setName("Test Course");
        learnerChange.setCourses(getUserCourseList());
        learnerChange.setContactEmailAddress("mailto:c.cooper@yahoo.com");

        ContactInformation contactInformation  = new ContactInformation();
        contactInformation.setContactinformationid(1L);

        ReflectionTestUtils.setField(hrService, "personService", personService);
        ReflectionTestUtils.setField(hrService, "contactInformationService",
                contactInformationService);
        Mockito.doReturn(contactInformation).when(contactInformationService)
        .getContactInformationByElectronicmailaddress(
        "c.cooper@yahoo.com");

        hrService.getContactInformation(learnerChange);
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
