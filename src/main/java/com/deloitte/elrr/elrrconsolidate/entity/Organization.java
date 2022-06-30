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
@Table(name = "ORGANIZATION")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Organization extends Auditable<String> {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long organizationid;

    /**
     *
     */
    @Column(name = "organizationname")
    private String organizationname;

    /**
     *
     */
    @Column(name = "organizationidentifier")
    private String organizationidentifier;

    /**
     *
    */
    @Column(name = "organizationidentificationcode")
    private String organizationidentificationcode;

    /**
     *
     */
    @Column(name = "organizationidentificationsystem")
    private String organizationidentificationsystem;

    /**
     *
     */
    @Column(name = "industrytypeidentifier")
    private String industrytypeidentifier;

    /**
     *
     */
    @Column(name = "organizationfein")
    private String organizationfein;

    /**
     *
     */
    @Column(name = "organizationdescription")
    private String organizationdescription;

    /**
     *
     */
    @Column(name = "parentorganization")
    private String parentorganization;

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
           return "Organization [id=" + organizationid
                + ", organizationName="
                + organizationname + ", industryIdentifier="
                + industrytypeidentifier + ", parentOrganization="
                + parentorganization
                + ", organizationidentificationsystem="
                + organizationidentificationsystem
                + ", organizationIdentifier="
                + organizationidentifier + ", organizationfein="
                + organizationfein + ", organizationIdentificationcode="
                + organizationidentificationcode
                + ", organizationdescription=" + organizationdescription + "]";
    }
}
