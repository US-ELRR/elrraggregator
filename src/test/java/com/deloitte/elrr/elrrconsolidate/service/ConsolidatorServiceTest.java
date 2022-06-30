package com.deloitte.elrr.elrrconsolidate.service;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.elrrconsolidate.jpa.service.LearnerProfileSvc;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)

public class ConsolidatorServiceTest {
    /**
    *
    */
    @Mock
    private LearnerProfileSvc mockLearnerProfileSvc;
    @Test
    void testConsolidate() {
        assertNotNull(mockLearnerProfileSvc.consolidate());
    }
}
