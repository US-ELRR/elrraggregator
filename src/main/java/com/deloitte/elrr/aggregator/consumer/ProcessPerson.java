package com.deloitte.elrr.aggregator.consumer;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.yetanalytics.xapi.model.AbstractActor;
import com.yetanalytics.xapi.model.AbstractObject;
import com.yetanalytics.xapi.model.Account;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessPerson {

  @Autowired private EmailSvc emailService;

  @Autowired private IdentitySvc identityService;

  @Autowired private PersonSvc personService;

  @Value("${lang.codes}")
  private String[] namLang = new String[10];

  private static String updatedBy = "ELRR";

  public Person processPerson(Statement statement) throws Exception {

    Person person = null;

    try {

      Account account = null;

      // Get Actor
      AbstractActor actor = getActor(statement);

      // Get Account
      if (actor != null) {
        account = getAccount(actor);
      }

      log.info("Process person.");

      // Get Person
      person = getPerson(actor, account);

      // If Person doesn't exist
      if (person == null) {
        person = createNewPerson(actor, account);
      }

    } catch (Exception e) {
      log.error("Exception while processing person.");
      log.error(e.getMessage());
      e.printStackTrace();
    }

    return person;
  }

  /**
   * @param statement
   * @return AbstractActor
   */
  public AbstractActor getActor(Statement statement) {
    AbstractActor actor = (AbstractActor) statement.getActor();
    return actor;
  }

  /**
   * @param statement
   * @return Account
   */
  public Account getAccount(AbstractActor actor) {
    Account account = actor.getAccount();
    return account;
  }

  /**
   * @param actor
   * @param Person
   */
  public Person getPerson(AbstractActor actor, Account account) {

    Person person = null;

    // Does person exist
    String ifi =
        Identity.createIfi(
            actor.getMbox_sha1sum(),
            actor.getMbox(),
            actor.getOpenid(),
            (account != null) ? account.getHomePage() : null,
            (account != null) ? account.getName() : null);

    Identity identity = identityService.getByIfi(ifi);

    // If person already exists
    if (identity != null) {
      person = identity.getPerson();
      log.info("Person " + person.getName() + " exists.");
    }

    return person;
  }

  /**
   * @param statement
   * @return AbstractObject
   */
  public AbstractObject getObject(Statement statement) {
    AbstractObject obj = statement.getObject();
    return obj;
  }

  /**
   * @param actor
   * @param account
   * @return Person
   */
  public Person createNewPerson(AbstractActor actor, Account account) {

    Email email = null;

    // If email
    if (actor.getMbox() != null && actor.getMbox().length() > 0) {
      email = createEmail(actor);
    }

    Person person = createPerson(actor, email);

    // If person created
    if (person != null) {
      Identity identity = createIdentity(person, actor, account);
    }

    return person;
  }

  /**
   * @param actor
   * @param email
   * @return Person
   */
  public Person createPerson(AbstractActor actor, Email email) {
    log.info("Creating new person.");
    Person person = new Person();
    person.setName(actor.getName());
    // If email
    if (email != null) {
      person.setEmailAddresses(new HashSet<Email>()); // Populate person_email
      person.getEmailAddresses().add(email);
    }
    person.setUpdatedBy(updatedBy);
    personService.save(person);
    log.info("Person " + person.getName() + " created.");
    return person;
  }

  /**
   * @param person
   * @param actor
   * @param account
   * @return Identity
   */
  public Identity createIdentity(Person person, AbstractActor actor, Account account) {
    log.info("Creating new identity.");
    Identity identity = new Identity();
    identity.setMboxSha1Sum(actor.getMbox_sha1sum());
    identity.setMbox(actor.getMbox());
    identity.setOpenid(actor.getOpenid());
    identity.setPerson(person);
    if (account != null) {
      identity.setHomePage(account.getHomePage());
      identity.setName(account.getName());
    }
    identity.setUpdatedBy(updatedBy);
    identityService.save(identity);
    log.info("Identity " + identity.getIfi() + " created.");
    return identity;
  }

  /**
   * @param actor
   * @return Email
   */
  public Email createEmail(AbstractActor actor) {
    log.info("Creating new email.");
    Email email = new Email();
    email.setEmailAddress(actor.getMbox());
    email.setEmailAddressType("primary");
    email.setUpdatedBy(updatedBy);
    emailService.save(email);
    log.info("Email " + email.getEmailAddress() + " created.");
    return email;
  }
}
