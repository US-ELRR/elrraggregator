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

import com.deloitte.elrr.elrrconsolidate.dto.CourseCompetency;
import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;
import com.deloitte.elrr.elrrconsolidate.entity.LearnerProfile;
import com.deloitte.elrr.elrrconsolidate.jpa.service.LearnerProfileSvc;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ConsolidatorServiceTest {
    /**
    *
    */
    @Mock
    private LearnerProfileSvc mockLearnerProfileSvc;

    /**
     *
     */
    @Test
    void testConsolidate() {
        ConsolidatorService consolidatorService = new ConsolidatorService();
        ContactInformation contactInforation = new ContactInformation();
        contactInforation.setContactinformationid(1L);
        ReflectionTestUtils.setField(consolidatorService,
                "learnerProfileService", mockLearnerProfileSvc);
        consolidatorService.consolidate(contactInforation,
                getCourseCompetencyList());
    }

    /**
    *
    */
   @Test
   void testConsolidateWithProfile() {
       ConsolidatorService consolidatorService = new ConsolidatorService();
       ContactInformation contactInforation = new ContactInformation();
       contactInforation.setContactinformationid(1L);
       contactInforation.setPersonid(1L);
       LearnerProfile learnerProfile = new LearnerProfile();
       learnerProfile.setPersonid(1L);

       ReflectionTestUtils.setField(consolidatorService,
               "learnerProfileService", mockLearnerProfileSvc);

       Mockito.doReturn(learnerProfile).when(mockLearnerProfileSvc)
       .getLearnerProfileByPersonIdCourseId(1L, 1L);
       consolidatorService.consolidate(contactInforation,
               getCourseCompetencyList());
   }

   /**
   *
   */
  @Test
  void testConsolidateWithException() {
      ConsolidatorService consolidatorService = new ConsolidatorService();
      ContactInformation contactInforation = new ContactInformation();
      contactInforation.setContactinformationid(1L);
      contactInforation.setPersonid(1L);
      LearnerProfile learnerProfile = new LearnerProfile();
      learnerProfile.setPersonid(1L);

      ReflectionTestUtils.setField(consolidatorService,
              "learnerProfileService", mockLearnerProfileSvc);

      Mockito.doReturn(learnerProfile).when(mockLearnerProfileSvc)
      .getLearnerProfileByPersonIdCourseId(1L, 1L);
      consolidatorService.consolidate(contactInforation,
              null);
  }
    /**
    *
    * @return List<CourseCompetency>
    */
   private static List<CourseCompetency>  getCourseCompetencyList() {
       List<CourseCompetency> courseCompetencies = new ArrayList<>();
       CourseCompetency courseCompetency = new CourseCompetency();
       courseCompetency.setCompetencyId(1L);
       courseCompetency.setCourseId(1L);
       courseCompetency.setStatus("OPEN");
       courseCompetencies.add(courseCompetency);
       return courseCompetencies;
   }
}
