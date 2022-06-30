package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.ELRRAuditLog;
import com.deloitte.elrr.elrrconsolidate.repository.ELRRAuditLogRepository;

@Service
public class ELRRAuditLogService implements CommonSvc<ELRRAuditLog, Long> {

    /**
     *
     */
    @Autowired
    private ELRRAuditLogRepository elrrAuditLogRepository;

    /**
     *
     */
    @Override
    public Long getId(final ELRRAuditLog entity) {

        return null;
    }

    /**
     *
     */
    @Override
    public CrudRepository<ELRRAuditLog, Long> getRepository() {

        return elrrAuditLogRepository;
    }

}
