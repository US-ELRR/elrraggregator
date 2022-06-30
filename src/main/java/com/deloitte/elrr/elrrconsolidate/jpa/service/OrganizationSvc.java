/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.Organization;
import com.deloitte.elrr.elrrconsolidate.repository.OrganizationRepository;

/**
 * @author mnelakurti
 *
 */

@Service
public class OrganizationSvc implements CommonSvc<Organization, Long> {

    /**
     *
     */
    private final OrganizationRepository organizationRepository;

    /**
     *
     * @param neworganizationRepository
     */
    public OrganizationSvc(final OrganizationRepository
            neworganizationRepository) {
        this.organizationRepository = neworganizationRepository;
    }

    /**
     *
     */
    @Override
    public CrudRepository<Organization, Long> getRepository() {
        return this.organizationRepository;
    }

    /**
     *
     */
    @Override
    public Long getId(final Organization organization) {
        return organization.getOrganizationid();
    }

    /**
     *
     */
    @Override
    public Organization save(final Organization organization) {
        return CommonSvc.super.save(organization);
    }

}
