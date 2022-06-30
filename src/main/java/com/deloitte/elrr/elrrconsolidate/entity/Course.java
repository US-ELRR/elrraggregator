package com.deloitte.elrr.elrrconsolidate.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COURSE")
//, schema="CMTR")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Course extends Auditable<String> {

    /**
    *
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long courseid;

    /**
    *
    */
    @Column(name = "coursetitle")
    private String name;

    /**
    *
    */
    @Column(name = "coursesubjectmatter")
    private String coursesubjectmatter;

    /**
    *
    */

    @Column(name = "coursesubjectabbreviation")
    private String coursesubjectabbreviation;

    /**
    *
    */
    @Column(name = "courseidentifier")
    private String courseidentifier;

    /**
    *
    */
    @Column(name = "courselevel")
    private String courselevel;

    /**
    *
    */
    @Column(name = "coursenumber")
    private String coursenumber;

    /**
    *
    */
    @Column(name = "courseinstructionmethod")
    private String courseinstructionmethod;

    /**
    *
    */
    @Column(name = "coursestartdate")
    private Date coursestartdate;

    /**
    *
    */
    @Column(name = "courseenddate")
    private Date courseenddate;

    /**
    *
    */
    @Column(name = "courseenrollmentdate")
    private Date courseenrollmentdate;

    /**
    *
    */
    @Column(name = "courseacademicgrade")
    private String courseacademicgrade;

    /**
    *
    */
    @Column(name = "courseprovidername")
    private String courseprovidername;

    /**
    *
    */
    @Column(name = "departmentname")
    private String departmentname;

    /**
    *
    */
    @Column(name = "coursegradescalecode")
    private String coursegradescalecode;

    /**
    *
    */
    @Column(name = "coursemetadatarepository")
    private String coursemetadatarepository;

    /**
    *
    */
    @Column(name = "courselrsendpoint")
    private String courselrsendpoint;

    /**
    *
    */
    @Column(name = "coursedescription")
    private String coursedescription;

    /**
    *
    */
    @Column(name = "recordstatus")
    private String recordstatus;

}
