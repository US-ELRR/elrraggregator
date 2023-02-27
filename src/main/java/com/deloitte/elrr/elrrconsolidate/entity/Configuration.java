package com.deloitte.elrr.elrrconsolidate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
