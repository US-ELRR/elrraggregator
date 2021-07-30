package com.deloitte.elrr.elrrconsolidate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.dto.LearnerChange;
import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;
import com.deloitte.elrr.elrrconsolidate.entity.Person;
import com.deloitte.elrr.elrrconsolidate.jpa.service.ContactInformationSvc;
import com.deloitte.elrr.elrrconsolidate.jpa.service.PersonSvc;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class HRService {

	@Autowired
	PersonSvc personService;
	
	@Autowired
	ContactInformationSvc contactInformationService;
	
	
	public ContactInformation getContactInformation(LearnerChange learnerChange) {
		
		String key = getKey(learnerChange.getContactEmailAddress());
		ContactInformation contact = contactInformationService.getContactInformationByElectronicmailaddress(key);
		//TODO what is the criteria to get the data changed..is it every week or 2 weeks or something else
		if (contact == null) {
			log.info("contact information not found and creating a new one");
			contact = invokeExternalService(learnerChange, key);
			Person person = createPerson(contact,learnerChange);
			contact.setPersonid(person.getPersonid());
			contact.setElectronicmailaddresstype("Personal");
			contact.setTelephonetype("Private");
			contact.setEmergencycontact("Email");
			contact.setIsprimaryindicator("Y");
			contactInformationService.save(contact);
		} else {
			log.info("contact information and person found "+contact.getContactinformationid() +
					"personId "+contact.getPersonid());
		}
		return contact;
	}
	private Person createPerson(ContactInformation contact,LearnerChange learnerChange) {
		log.info("creating new person");
		Person person = new Person();
		person.setName(learnerChange.getName());
		String tokens[] = learnerChange.getName().split(" ");
		person.setFirstname(tokens[0]);
		person.setLastname(tokens[1]);
		personService.save(person);
		return person;
	}
	private ContactInformation invokeExternalService(LearnerChange learnerChange, String key) {
		log.info("invoking externalService to get Contact info");
		ContactInformation contact = new ContactInformation();
		contact.setElectronicmailaddress(key);
		contact.setContactinformation("Email");
		contact.setTelephonenumber("800-922-0222");
		return contact;
	}
	private String getKey(String contactEmailAddress) {
		String key = contactEmailAddress.replace("mailto:", "");
		return key;
	}
	
	 	
	 
}
