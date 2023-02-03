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
import com.deloitte.elrr.elrrconsolidate.entity.Person;
import com.deloitte.elrr.elrrconsolidate.repository.PersonalRepository;

/**
 * @author mnelakurti
 *
 */

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PersonSvcTest {

    /**
    *
    */
    @Mock
    private PersonalRepository personRepository;

    /**
     * @throws ResourceNotFoundException
     *
     */
    @Test
    void test() throws ResourceNotFoundException {
        PersonSvc personSvc = new PersonSvc(personRepository);
        Person person = new Person();
        person.setPersonid(1L);
        List<Person> personList = new ArrayList<>();
        personList.add(person);
        ReflectionTestUtils.setField(personSvc, "personalRepository",
                personRepository);
        Mockito.doReturn(person).when(personRepository).save(person);
        Mockito.doReturn(true).when(personRepository).existsById(1L);
        Mockito.doNothing().when(personRepository).deleteById(1L);

        personSvc.getId(person);
        personSvc.findAll();
        personSvc.get(1L);
        personSvc.save(person);
        personSvc.deleteAll();
        personSvc.delete(1L);
        personSvc.update(person);
        personSvc.saveAll(personList);
        person.setPersonid(2L);
        try {
            personSvc.update(person);
        } catch (Exception e) {

        }
    }
}
