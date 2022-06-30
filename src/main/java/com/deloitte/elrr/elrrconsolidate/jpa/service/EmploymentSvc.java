/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.Employment;
import com.deloitte.elrr.elrrconsolidate.repository.EmploymentRepository;

/**
 * @author mnelakurti
 *
 */

@Service
public class EmploymentSvc implements CommonSvc<Employment, Long> {

    /**
     *
     */
    private final EmploymentRepository employmentRepository;

    /**
     *
     * @param newemploymentRepository
     */
    public EmploymentSvc(final EmploymentRepository newemploymentRepository) {
        this.employmentRepository = newemploymentRepository;
    }

    /**
     *
     */
    @Override
    public CrudRepository<Employment, Long> getRepository() {
        return this.employmentRepository;
    }

    /**
     *
     */
    @Override
    public Long getId(final Employment employment) {
        return employment.getEmploymentid();
    }

    /**
     *
     */
    @Override
    public Employment save(final Employment employment) {
        return CommonSvc.super.save(employment);
    }

}
