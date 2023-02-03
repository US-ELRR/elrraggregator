/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.deloitte.elrr.elrraggregator.exception.ResourceNotFoundException;
import com.deloitte.elrr.elrrconsolidate.entity.Employment;
import com.deloitte.elrr.elrrconsolidate.repository.EmploymentRepository;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmploymentSvcTest {

    /**
    *
    */
    @Mock
    private EmploymentRepository employmentRepository;

    /**
     * @throws ResourceNotFoundException
     *
     */
    @Test
    void test() throws ResourceNotFoundException {
        EmploymentSvc employmentSvc = new EmploymentSvc(employmentRepository);
        Employment employment = new Employment();
        employment.setEmploymentid(1L);
        ReflectionTestUtils.setField(employmentSvc, "employmentRepository",
                employmentRepository);
        Mockito.doReturn(employment).when(employmentRepository)
                .save(employment);
        Mockito.doReturn(true).when(employmentRepository)
        .existsById(1L);
        Mockito.doNothing().when(employmentRepository)
        .deleteById(1L);
        employmentSvc.getId(employment);
        employmentSvc.findAll();
        employmentSvc.get(1L);
        employmentSvc.save(employment);
        employmentSvc.deleteAll();
        employmentSvc.delete(1L);
        employmentSvc.update(employment);
    }

}
