package com.deloitte.elrr.elrrconsolidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deloitte.elrr.elrrconsolidate.entity.RoleRelations;

@Repository
public interface RoleRelationsRepository
        extends JpaRepository<RoleRelations, Long> {

}
