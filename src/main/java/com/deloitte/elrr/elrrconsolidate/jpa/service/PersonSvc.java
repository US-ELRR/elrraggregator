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
	private final PersonalRepository personalRepository;

	public PersonSvc(final PersonalRepository personalRepository) {
		this.personalRepository = personalRepository;
	}

	@Override
	public CrudRepository<Person, Long> getRepository() {
		return this.personalRepository;
	}

	@Override
	public Long getId(Person person) {
		return person.getPersonid();
	}

	@Override
	public Person save(Person person) {
		return CommonSvc.super.save(person);
	}

}
