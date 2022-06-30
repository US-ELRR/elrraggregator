package com.deloitte.elrr.elrrconsolidate.service;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.elrrconsolidate.jpa.service.CompetencySvc;
import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.entity.Course;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)

public class CASSServiceTest {
    /**
    *
    */
    @Mock
    private CASSService mockCASSService;
    /**
     *
     */
    @Mock
    private CompetencySvc mockcompetencySvc;
    /**
     *
     */
    @Mock
    private LearnerChange mocklearnerChange;
    /**
     *
     */
    @Mock
    private Course mockcourses;

    @Test
    void testCASSService() {
        assertNotNull(mockCASSService.getCompetencies(mocklearnerChange, null));
    }
}
