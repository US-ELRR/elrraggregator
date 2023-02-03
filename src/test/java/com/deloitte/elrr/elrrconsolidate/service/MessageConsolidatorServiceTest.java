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
import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MessageConsolidatorServiceTest {

    /**
    *
    */
   @Mock
   private HRService hrService;

   /**
   *
   */
   @Mock
   private ECCService eccService;

   /**
    *
    */
   @Mock
   private CASSService cassService;

   /**
    *
    */
   @Mock
   private ConsolidatorService consolidatorService;

   /**
     * Test method for
     * {@link com.deloitte.elrr.elrrconsolidate.service.
     * MessageConsolidatorService#process
     * (com.deloitte.elrr.elrrconsolidate.dto.LearnerChange)}.
     */
    @Test
    void testProcess() {
        MessageConsolidatorService messageConsolidatorService
        = new MessageConsolidatorService();
        LearnerChange learnerChange = new LearnerChange();
        learnerChange.setName("Test Course");
        learnerChange.setCourses(getUserCourseList());
        learnerChange.setContactEmailAddress("mailto:c.cooper@yahoo.com");

        ContactInformation contactInformation  = new ContactInformation();
        contactInformation.setContactinformationid(1L);

        ReflectionTestUtils.setField(messageConsolidatorService, "hrService",
                hrService);
        ReflectionTestUtils.setField(messageConsolidatorService, "eccService",
                eccService);
        ReflectionTestUtils.setField(messageConsolidatorService, "cassService",
                cassService);
        ReflectionTestUtils.setField(messageConsolidatorService,
                "consolidatorService", consolidatorService);
        /*
         * Mockito.doReturn(contactInformation).when(contactInformationService)
         * .getContactInformationByElectronicmailaddress( "c.cooper@yahoo.com");
         */
        messageConsolidatorService.process(learnerChange);
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
