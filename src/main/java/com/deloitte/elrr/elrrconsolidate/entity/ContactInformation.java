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
@Table(name = "CONTACTINFORMATION")
//, schema="CMTR")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContactInformation extends Auditable<String> {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long contactinformationid;

    /**
     *
     */
    @Column(name = "personid")
    private long personid;

    /**
     *
    */
    @Column(name = "contactinformation")
    private String contactInformationData;

    /**
     *
     */
    @Column(name = "telephonenumber")
    private String telephonenumber;

    /**
     *
     */
    @Column(name = "isprimaryindicator")
    private String isprimaryindicator;

    /**
     *
     */
    @Column(name = "telephonetype")
    private String telephonetype;

    /**
     *
     */
    @Column(name = "electronicmailaddress")
    private String electronicmailaddress;

    /**
     *
     */
    @Column(name = "electronicmailaddresstype")
    private String electronicmailaddresstype;

    /**
     *
     */
    @Column(name = "emergencycontact")
    private String emergencycontact;

    /**
     *
     */
    @Column(name = "recordstatus")
    private String recordstatus;
}
