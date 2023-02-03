/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

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

import com.deloitte.elrr.elrraggregator.exception.ResourceNotFoundException;
import com.deloitte.elrr.elrrconsolidate.entity.LearnerProfile;
import com.deloitte.elrr.elrrconsolidate.repository.LearnerProfileRepository;

/**
 * @author mnelakurti
 *
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LearnerProfileSvcTest {

    /**
    *
    */
    @Mock
    private LearnerProfileRepository learnerProfileRepository;

    /**
     * @throws ResourceNotFoundException
     *
     */
    @Test
    void test() throws ResourceNotFoundException {
        LearnerProfileSvc learnerProfileSvc = new LearnerProfileSvc(
                learnerProfileRepository);
        LearnerProfile learnerProfile = new LearnerProfile();
        learnerProfile.setLearnerprofileid(1L);
        learnerProfile.setPersonid(1L);
        List<LearnerProfile> learnerProfileList = new ArrayList<>();
        learnerProfileList.add(learnerProfile);
        ReflectionTestUtils.setField(learnerProfileSvc,
                "learnerProfileRepository", learnerProfileRepository);
        Mockito.doReturn(learnerProfile).when(learnerProfileRepository)
                .save(learnerProfile);
        Mockito.doReturn(true).when(learnerProfileRepository).existsById(1L);
        Mockito.doNothing().when(learnerProfileRepository).deleteById(1L);
        Mockito.doReturn(learnerProfile).when(learnerProfileRepository)
        .getLearnerProfileByPersonIdCourseId(1L, 1L);

        learnerProfileSvc.getId(learnerProfile);
        learnerProfileSvc.findAll();
        learnerProfileSvc.get(1L);
        learnerProfileSvc.save(learnerProfile);
        learnerProfileSvc.deleteAll();
        learnerProfileSvc.delete(1L);
        learnerProfileSvc.getLearnerProfileByPersonIdCourseId(1L, 1L);
        learnerProfileSvc.consolidate();
        learnerProfileSvc.update(learnerProfile);
        learnerProfileSvc.saveAll(learnerProfileList);
    }

}
