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
@Table(name = "EMPLOYMENT")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Employment extends Auditable<String> {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long employmentid;

    /**
     *
     */
    @Column(name = "employername")
    private String employerName;

    /**
     *
     */
    @Column(name = "employerdepartment")
    private String employerdepartment;

    /**
     *
     */
    @Column(name = "hiredate")
    private Date hiredate;

    /**
     *
     */
    @Column(name = "employmentstartdate")
    private String employmentstartdate;

    /**
     *
     */
    @Column(name = "employmentenddate")
    private Date employmentenddate;


    /**
     *
     */
    @Column(name = "joblevel")
    private String joblevel;

    /**
     *
     */
    @Column(name = "occupation")
    private String occupation;

    /**
     *
     */
    @Column(name = "employed")
    private String employed;

    /**
     *
     */
    @Column(name = "primarycareercategory")
    private String primarycareercategory;

    /**
     *
     */
    @Column(name = "recordstatus")
    private String recordstatus;

}
