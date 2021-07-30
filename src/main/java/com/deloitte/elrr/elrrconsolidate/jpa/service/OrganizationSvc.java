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
	private final OrganizationRepository organizationRepository;

	public OrganizationSvc(final OrganizationRepository organizationRepository) {
		this.organizationRepository =organizationRepository;
	}

	@Override
	public CrudRepository<Organization, Long> getRepository() {
		return this.organizationRepository;
	}

	@Override
	public Long getId(Organization organization) {
		return organization.getOrganizationid();
	}

	@Override
	public Organization save(Organization organization) {
		return CommonSvc.super.save(organization);
	}

}
