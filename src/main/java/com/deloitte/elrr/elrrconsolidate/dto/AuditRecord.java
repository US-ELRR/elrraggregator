package com.deloitte.elrr.elrrconsolidate.dto;

import java.util.List;

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
public class AuditRecord {

    /**
     *
     */
    private Long auditId;

    /**
     *
     */
    private List<Long> auditDeailIds;
}
