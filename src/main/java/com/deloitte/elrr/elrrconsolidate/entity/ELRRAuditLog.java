package com.deloitte.elrr.elrrconsolidate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.hypersistence.utils.hibernate.type.json.JsonType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Convert;

@Entity
@Table(name = "ELRRAUDITLOG")
// @Convert(converter = JsonType.class)
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ELRRAuditLog extends Auditable<String> {

    /**
    *
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long auditlogid;

    /**
    *
    */
    private long syncid;

    /**
    *
    */
    // @Convert(converter = JsonType.class)
    @Column(columnDefinition = "jsonb")
    private String payload;

}
