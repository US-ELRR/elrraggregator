package com.deloitte.elrr.elrrconsolidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deloitte.elrr.elrrconsolidate.entity.Organization;

@Repository
public interface OrganizationRepository
        extends JpaRepository<Organization, Long> {

}
