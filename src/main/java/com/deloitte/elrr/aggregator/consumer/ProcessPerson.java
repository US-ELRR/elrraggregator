package com.deloitte.elrr.aggregator.consumer;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.deloitte.elrr.elrraggregator.exception.PersonNotFoundException;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.yetanalytics.xapi.model.AbstractActor;
import com.yetanalytics.xapi.model.Account;
import com.yetanalytics.xapi.model.Statement;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ProcessPerson {

    @Autowired
    private EmailSvc emailService;

    @Autowired
    private IdentitySvc identityService;

    @Autowired
    private PersonSvc personService;

    /**
     * @param statement
     * @return Person
     * @throws PersonNotFoundException
     */
    public Person processPerson(Statement statement) {

        AbstractActor actor = null;
        Person person = null;
        Account account = null;

        // Get Actor and account
        if (statement.getActor() instanceof AbstractActor) {

            actor = (AbstractActor) statement.getActor();
            account = actor.getAccount();

        } else {

            log.error("Actor not found.");
            throw new PersonNotFoundException("Actor not found.");

        }

        log.info("Process person.");

        // Get Person
        person = getPerson(actor, account);

        // If Person doesn't exist
        if (person == null) {
            person = createPerson(actor, account);
        }

        // If error creating Person
        if (person == null) {
            throw new PersonNotFoundException("Person not found.");
        }

        return person;
    }

    /**
     * @param actor
     * @param account
     * @return person
     */
    public Person getPerson(AbstractActor actor, Account account) {

        Person person = null;
        String mBox = null;
        String mboxSha1sum = null;
        String openId = null;

        if (actor.getMbox() != null) {
            mBox = actor.getMbox().toString();
        }

        if (actor.getMbox_sha1sum() != null) {
            mboxSha1sum = actor.getMbox_sha1sum().toString();
        }

        if (actor.getOpenid() != null) {
            openId = actor.getOpenid().toString();
        }

        // Does person exist
        String ifi = Identity.createIfi(mboxSha1sum, mBox, openId,
                (account != null) ? account.getHomePage().toString() : null,
                (account != null) ? account.getName() : null);

        Identity identity = identityService.getByIfi(ifi);

        // If person already exists
        if (identity != null) {
            person = identity.getPerson();
            if (person != null) {
                log.info("Person " + person.getName() + " exists.");
            }
        }

        return person;
    }

    /**
     * @param actor
     * @param account
     * @return Person
     */
    public Person createPerson(AbstractActor actor, Account account) {

        log.info("Creating new person.");

        Email email = null;

        // If email
        if (actor.getMbox() != null) {
            email = createEmail(actor.getMbox().toString());
        }

        Person person = new Person();
        person.setName(actor.getName());
        personService.save(person);

        // If email
        if (email != null) {
            person.setEmailAddresses(new HashSet<Email>());
            person.getEmailAddresses().add(email);
        }

        Identity identity = createIdentity(person, actor, account);

        person.setIdentities(new HashSet<Identity>());
        person.getIdentities().add(identity);

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
    public Identity createIdentity(Person person, AbstractActor actor,
            Account account) {

        String mBox = null;
        String mboxSha1sum = null;
        String openId = null;

        if (actor.getMbox() != null) {
            mBox = actor.getMbox().toString();
        }

        if (actor.getMbox_sha1sum() != null) {
            mboxSha1sum = actor.getMbox_sha1sum().toString();
        }

        if (actor.getOpenid() != null) {
            openId = actor.getOpenid().toString();
        }

        log.info("Creating new identity.");

        Identity identity = new Identity();
        identity.setMboxSha1Sum(mboxSha1sum);
        identity.setMbox(mBox);
        identity.setOpenid(openId);
        identity.setPerson(person);

        if (account != null) {
            identity.setHomePage(account.getHomePage().toString());
            identity.setName(account.getName());
        }

        identityService.save(identity);

        log.info("Identity " + identity.getIfi() + " created.");

        return identity;
    }

    /**
     * @param emailAddress
     * @return email
     */
    public Email createEmail(String emailAddress) {
        log.info("Creating new email.");
        Email email = new Email();
        email.setEmailAddress(emailAddress);
        email.setEmailAddressType("primary");
        emailService.save(email);
        log.info("Email " + emailAddress + " created.");
        return email;
    }
}
