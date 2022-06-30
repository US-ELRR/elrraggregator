package com.deloitte.elrr.elrrconsolidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deloitte.elrr.elrrconsolidate.entity.Person;

@Repository
public interface PersonalRepository extends JpaRepository<Person, Long> {

}
