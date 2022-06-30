package com.deloitte.elrr.elrrconsolidate.jpa.service;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.elrr.elrrconsolidate.repository.CompetencyRepository;
/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
public class CompetencySvcTest {
    /**
    *
    */
    @Mock
    private CompetencySvc mockCompetencySvc;
    /**
    *
    */
    @Mock
    private CompetencyRepository mockCompetencyRepository;

    @Test
    void test() {
        assertNotNull(mockCompetencySvc.getId(null));
    }
}
