/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.Person;
import com.deloitte.elrr.elrrconsolidate.repository.PersonalRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mnelakurti
 *
 */

@Service
@Slf4j
public class PersonSvc implements CommonSvc<Person, Long> {

    /**
     *
     */
    private final PersonalRepository personalRepository;


    /**
     *
     * @param newpersonalRepository
     */
    public PersonSvc(final PersonalRepository newpersonalRepository) {
        this.personalRepository = newpersonalRepository;
    }

    /**
     *
     */
    @Override
    public CrudRepository<Person, Long> getRepository() {
        return this.personalRepository;
    }

    /**
     *
     */
    @Override
    public Long getId(final Person person) {
        return person.getPersonid();
    }

    /**
     *
     */
    @Override
    public Person save(final Person person) {
        return CommonSvc.super.save(person);
    }

}
