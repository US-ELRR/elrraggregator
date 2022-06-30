package com.deloitte.elrr.elrrconsolidate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
public class MessageVO {

    /**
    *
    */
    private AuditRecord auditRecord;

    /**
    *
    */
    private LearnerChange learnerChange;

}
