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
@Table(name = "Role")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role extends Auditable<String> {

    /**
    *
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "roleid")
    private long roleid;

    /**
    *
    */
    @Column(name = "rolename")
    private String roleName;

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
        return "";
    }

}
