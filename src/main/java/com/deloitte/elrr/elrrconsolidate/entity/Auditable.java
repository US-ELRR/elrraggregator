package com.deloitte.elrr.elrrconsolidate.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Auditable<U> {

    /**
    *
    */
    @Column(name = "inserteddate", updatable = false)
    @CreationTimestamp
    private Timestamp inserteddate;

    /**
     *
     */
    @Column(name = "updatedby")
    @LastModifiedBy
    private U updatedBy;

    /**
     *
     */
    @Column(name = "lastmodified")
    @UpdateTimestamp
    private Timestamp lastmodified;
}
