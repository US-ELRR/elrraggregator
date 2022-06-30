package com.deloitte.elrr.elrrconsolidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deloitte.elrr.elrrconsolidate.entity.ELRRAuditLog;

@Repository
public interface ELRRAuditLogRepository
        extends JpaRepository<ELRRAuditLog, Long> {

}
