package com.deloitte.ellr.ellrconsolidate.jpa.service;

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
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.repository.PersonRepository;

/**
 * @author mnelakurti
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PersonSvcTest {

  @Mock PersonSvc personService;
  @Mock PersonRepository personRepository;

  /**
   * @throws ResourceNotFoundException
   */
  @Test
  void test() throws ResourceNotFoundException {
    PersonSvc personSvc = new PersonSvc(personRepository);
    Person person = new Person();
    List<Person> personList = new ArrayList<>();
    personList.add(person);
    ReflectionTestUtils.setField(personSvc, "personRepository", personRepository);
    Mockito.doReturn(person).when(personRepository).save(person);
    Mockito.doReturn(true).when(personRepository).existsById(person.getId());
    Mockito.doNothing().when(personRepository).deleteById(person.getId());

    personSvc.getId(person);
    personSvc.findAll();
    personSvc.get(person.getId());
    personSvc.save(person);
    personSvc.deleteAll();
    personSvc.delete(person.getId());
    personSvc.update(person);
    personSvc.saveAll(personList);
    // person.setPersonid(2L);
    try {
      personSvc.update(person);
    } catch (Exception e) {

    }
  }
}
