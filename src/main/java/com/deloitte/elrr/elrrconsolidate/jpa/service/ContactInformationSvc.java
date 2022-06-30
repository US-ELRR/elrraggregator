/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;
import com.deloitte.elrr.elrrconsolidate.repository.ContactInformationRepository;

/**
 * @author mnelakurti
 *
 */

@Service
public class ContactInformationSvc
        implements CommonSvc<ContactInformation, Long> {

    /**
     *
     */
    private final ContactInformationRepository contactInformationRepository;

    /**
     *
     * @param newcontactInformationRepository
     */
    public ContactInformationSvc(
            final ContactInformationRepository
            newcontactInformationRepository) {
        this.contactInformationRepository = newcontactInformationRepository;
    }

    /**
     *
     *
     */
    @Override
    public CrudRepository<ContactInformation, Long> getRepository() {
        return this.contactInformationRepository;
    }

    /**
     * @param courseaccreditation
     */
    @Override
    public Long getId(final ContactInformation courseaccreditation) {
        return courseaccreditation.getContactinformationid();
    }

    /**
     * @param contactInformation
     */
    @Override
    public ContactInformation save(
            final ContactInformation contactInformation) {
        return CommonSvc.super.save(contactInformation);
    }

    /**
     *
     * @param courseidentifier
     * @return ContactInformation contactInformation
     */
    public ContactInformation getContactInformationByElectronicmailaddress(
            final String courseidentifier) {
        return this.contactInformationRepository.findByEmail(courseidentifier);
    }

}
