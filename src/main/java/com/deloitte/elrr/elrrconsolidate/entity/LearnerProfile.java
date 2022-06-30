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
@Table(name = "LEARNERPROFILE")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LearnerProfile extends Auditable<String> {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long learnerprofileid;

    /**
     *
     */
    @Column(name = "personid")
    private Long personid;

    /**
     *
     */
    @Column(name = "learneraddressid")
    private Long learneraddressid;

    /**
    *
    */
    @Column(name = "contactinformationid")
    private Long contactinformationid;

    /**
    *
    */
    @Column(name = "employmentid")
    private Long employmentid;

    /**
    *
    */
    @Column(name = "positionid")
    private Long positionid;

    /**
    *
    */
    @Column(name = "citizenshipid")
    private Long citizenshipid;

    /**
    *
    */
    @Column(name = "studentid")
    private Long studentid;

    /**
    *
    */
    @Column(name = "courseid")
    private Long courseid;

    /**
    *
    */
    @Column(name = "courseaccreditationid")
    private Long courseaccreditationid;

    /**
    *
    */
    @Column(name = "competencyid")
    private Long competencyid;

    /**
    *
    */
    @Column(name = "credentialid")
    private Long credentialid;

    /**
    *
    */
    @Column(name = "organizationid")
    private Long organizationid;

    /**
    *
    */
    @Column(name = "organizationaddressid")
    private Long organizationaddressid;

    /**
    *
    */
    @Column(name = "accreditationid")
    private Long accreditationid;

    /**
    *
    */
    @Column(name = "activitystatus")
    private String activitystatus;

    /**
    *
    */
    @Column(name = "recordstatus")
    private String recordstatus;
}
