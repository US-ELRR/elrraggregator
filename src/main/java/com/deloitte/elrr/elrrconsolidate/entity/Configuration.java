package com.deloitte.elrr.elrrconsolidate.entity;

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
@Table(name = "CONFIGURATION")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Configuration extends Auditable<String> {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long configurationid;

    /**
     *
     */
    @Column(name = "configurationname")
    private String configurationname;

    /**
     *
     */
    @Column(name = "configurationvalue")
    private String configurationvalue;

    /**
     *
     */
    @Column(name = "frequency")
    private String frequency;

    /**
     *
     */
    @Column(name = "starttime")
    private String starttime;

    /**
     *
     */
    @Column(name = "primarycontact")
    private String primarycontact;

    /**
     *
     */
    @Column(name = "primaryemail")
    private String primaryemail;

    /**
     *
     */
    @Column(name = "primaryorgname")
    private String primaryorgname;

    /**
     *
     */
    @Column(name = "primaryphone")
    private String primaryphone;

    /**
     *
     */
    @Column(name = "secondarycontact")
    private String secondarycontact;

    /**
     *
     */
    @Column(name = "secondaryemail")
    private String secondaryemail;

    /**
     *
     */
    @Column(name = "secondaryorgname")
    private String secondaryorgname;

    /**
     *
     */
    @Column(name = "secondaryphone")
    private String secondaryphone;

    /**
     *
     */
    @Column(name = "recordstatus")
    private String recordstatus;
}
