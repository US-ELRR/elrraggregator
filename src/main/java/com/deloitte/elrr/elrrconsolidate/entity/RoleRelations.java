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
@Table(name = "Rolerelations")
//, schema="CMTR")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoleRelations extends Auditable<String> {

    /**
    *
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rolerelationsid")
    private long rolerelationsid;

    /**
    *
    */
    @Column(name = "parentroleid")
    private long parentRoleid;

    /**
    *
    */
    @Column(name = "parentpersonid")
    private long parentPersonid;

    /**
    *
    */
    @Column(name = "childroleid")
    private long childRoleid;

    /**
    *
    */
    @Column(name = "childpersonid")
    private long childPersonid;

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
        return "id " + rolerelationsid;
    }

}
