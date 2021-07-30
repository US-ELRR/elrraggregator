package com.deloitte.elrr.elrrconsolidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;

@Repository
public interface ContactInformationRepository  extends JpaRepository<ContactInformation, Long>{
	
	@Query("SELECT c FROM ContactInformation c WHERE LOWER(c.electronicmailaddress) = LOWER(:electronicmailaddress)")
	public ContactInformation findByEmail(final String electronicmailaddress);

}
