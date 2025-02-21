package com.deloitte.elrr.elrrconsolidate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HRService {

  @Autowired private PersonSvc personService;

  @Autowired private EmailSvc emailService;

  /**
   * @param actorName
   * @param actorEmail
   * @return
   */
  public Person createPerson(final String actorName, final String actorEmail) {

    // Create new Email
    log.info("creating new email");
    Email email = new Email();
    email.setEmailAddress(actorEmail);
    email.setEmailAddressType("primary");
    emailService.save(email);

    // Create new Person
    log.info("creating new person");
    Person person = new Person();

    String[] tokens = actorName.split(" ");

    person.setFirstName(tokens[0]);

    // If first name only
    if (tokens.length == 1) {
      person.setMiddleName("");
      person.setLastName("");
    }

    // If first and last name
    if (tokens.length == 2) {
      person.setMiddleName("");
      person.setLastName(tokens[1]);
    }

    // If first, middle and last name
    if (tokens.length == 3) {
      person.setMiddleName(tokens[1]);
      person.setLastName(tokens[2]);
    }

    person.getEmailAddresses().add(email);
    personService.save(person);
    return person;
  }
}
