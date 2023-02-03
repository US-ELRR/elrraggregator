package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.deloitte.elrr.elrrconsolidate.entity.Competency;
import com.deloitte.elrr.elrrconsolidate.repository.CompetencyRepository;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CompetencySvcTest {

    /**
    *
    */
    @Mock
    private CompetencyRepository mockCompetencyRepository;

    @Test
    void test() {
        CompetencySvc competencySvc = new CompetencySvc(
                mockCompetencyRepository);
        Competency competency = new Competency();
        competency.setCompetencyid(1L);

        ReflectionTestUtils.setField(competencySvc, "competencyRepository",
                mockCompetencyRepository);
        Mockito.doReturn(competency).when(mockCompetencyRepository)
        .findByCompetencyName("");
        Mockito.doReturn(competency).when(mockCompetencyRepository)
        .save(competency);
        competencySvc.findByCompetencyName("");
        competencySvc.getId(competency);
        competencySvc.get(1L);
        competencySvc.save(competency);
    }

}
