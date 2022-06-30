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
@Table(name = "COMPETENCY")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Competency extends Auditable<String> {

    /**
     *
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long competencyid;

    /**
     *
     */
    @Column(name = "competencyframeworktitle")
    private String competencyframeworktitle;

    /**
     *
     */
    @Column(name = "competencyframeworkversion")
    private String competencyframeworkversion;

    /**
     *
     */
    @Column(name = "competencyframeworkidentifier")
    private String competencyframeworkidentifier;

    /**
     *
     */
    @Column(name = "competencyframeworkdescription")
    private String competencyframeworkdescription;

    /**
     *
     */
    @Column(name = "competencyframeworksubject")
    private String competencyframeworksubject;

    /**
     *
     */
    @Column(name = "competencyframeworkvalidstartdate")
    private Date competencyframeworkvalidstartdate;

    /**
     *
     */
    @Column(name = "competencyframeworkvalidenddate")
    private Date competencyframeworkvalidenddate;

    /**
     *
     */
    @Column(name = "competencydefinitionidentifier")
    private String competencydefinitionidentifier;

    /**
     *
     */
    @Column(name = "competencydefinitionidentifierurl")
    private String competencydefinitionidentifierurl;

    /**
     *
     */
    @Column(name = "competencytaxonomyid")
    private String competencytaxonomyid;

    /**
     *
     */
    @Column(name = "competencydefinitionvalidstartdate")
    private Date competencydefinitionvalidstartdate;

    /**
     *
     */
    @Column(name = "competencydefinitionvalideenddate")
    private Date competencydefinitionvalideenddate;

    /**
     *
     */
    @Column(name = "competencydefinitionparentidentifier")
    private String competencydefinitionparentidentifier;

    /**
     *
     */
    @Column(name = "competencydefinitionparenturl")
    private String competencydefinitionparenturl;

    /**
     *
     */
    @Column(name = "competencydescriptionparentcode")
    private String competencydescriptionparentcode;

    /**
     *
     */
    @Column(name = "competencydefinitioncode")
    private String competencydefinitioncode;

    /**
     *
     */
    @Column(name = "competencydefinitionstatement")
    private String competencydefinitionstatement;

    /**
     *
     */
    @Column(name = "competencydefinitiontypeurl")
    private String competencydefinitiontypeurl;

    /**
     *
     */
    @Column(name = "competencydefinitiontype")
    private String competencydefinitiontype;

    /**
     *
     */
    @Column(name = "recordstatus")
    private String recordstatus;

}
