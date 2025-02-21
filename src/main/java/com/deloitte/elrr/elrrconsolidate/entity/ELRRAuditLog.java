package com.deloitte.elrr.elrrconsolidate.entity;

import com.deloitte.elrr.entity.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ELRRAUDITLOG")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ELRRAuditLog extends Auditable<String> {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "elrrauditlog_seq")
  @SequenceGenerator(name = "elrrauditlog_seq", sequenceName = "elrrauditlog_seq")
  @Column(name = "ELRRAUDITLOGID")
  private long auditlogid;

  private long syncid;
}
