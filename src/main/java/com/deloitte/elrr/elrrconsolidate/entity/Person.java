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
@Table(name = "PERSON")
//,schema="CMTR")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Person extends Auditable<String> {

    /**
    *
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long personid;

    /**
    *
    */
    @Column(name = "name")
    private String name;

    /**
    *
    */
    @Column(name = "firstname")
    private String firstname;

    /**
    *
    */
    @Column(name = "middlename")
    private String middlename;

    /**
    *
    */
    @Column(name = "lastname")
    private String lastname;

    /**
    *
    */
    @Column(name = "nameprefix")
    private String nameprefix;

    /**
    *
    */
    @Column(name = "titleaffixcode")
    private String titleaffixcode;

    /**
    *
    */
    @Column(name = "namesuffix")
    private String namesuffix;

    /**
    *
    */
    @Column(name = "qualificationaffixcode")
    private String qualificationaffixcode;

    /**
    *
    */
    @Column(name = "maidenname")
    private String maidenname;

    /**
    *
    */
    @Column(name = "preferredname")
    private String preferredname;

    /**
    *
    */
    @Column(name = "humanresourceidentifier")
    private String humanresourceidentifier;

    /**
    *
    */
    @Column(name = "personnelidentificationsystem")
    private String personnelidentificationsystem;

    /**
    *
    */
    @Column(name = "birthdate")
    private Date birthdate;

    /**
    *
    */
    @Column(name = "sex")
    private String sex;

    /**
    *
    */
    @Column(name = "primarylanguage")
    private String primarylanguage;

    /**
    *
    */
    @Column(name = "militaryveteranindicator")
    private String militaryveteranindicator;

    /**
    *
    */
    @Column(name = "recordstatus")
    private String recordstatus;

    /**
    *
    */
    @Override
    public String toString() {

        return "Person[id=" + personid + ",name=" + name + ",firstName="
                + firstname + ",middleName=" + middlename
                + ",lastName=" + lastname + ",namePrefix=" + nameprefix
                + ",titleAffixcode=" + titleaffixcode
                + ",qualificationAffixcode=" + qualificationaffixcode
                + ",maidenName=" + maidenname + ",preferredName="
                + preferredname + ",humanResourceIdentifier="
                + humanresourceidentifier
                + ",personnelIdentificationSystem="
                + personnelidentificationsystem
                + ",birthdate=" + birthdate
                + ",sex=" + sex + ",primaryLanguage="
                + primarylanguage + ",militaryVeteranindicator="
                + militaryveteranindicator + ",recordStatus="
                + recordstatus + "]";
    }

}
