package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.deloitte.elrr.elrraggregator.exception.ResourceNotFoundException;
import com.deloitte.elrr.elrrconsolidate.entity.ELRRAuditLog;
import com.deloitte.elrr.elrrconsolidate.repository.ELRRAuditLogRepository;

/**
 *
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ELRRAuditLogServiceTest {

    /**
    *
    */
    @Mock
    private ELRRAuditLogRepository elrrAuditLogRepository;

    /**
     * @throws ResourceNotFoundException
     *
     */
    @Test
    void test() throws ResourceNotFoundException {
        ELRRAuditLogService eLRRAuditLogServic = new ELRRAuditLogService();
        ELRRAuditLog eLRRAuditLog = new ELRRAuditLog();
        eLRRAuditLog.setAuditlogid(1L);
        ReflectionTestUtils.setField(eLRRAuditLogServic,
                "elrrAuditLogRepository", elrrAuditLogRepository);
        Mockito.doReturn(eLRRAuditLog).when(elrrAuditLogRepository)
                .save(eLRRAuditLog);
        eLRRAuditLogServic.getId(eLRRAuditLog);
        eLRRAuditLogServic.findAll();
        eLRRAuditLogServic.get(1L);
        eLRRAuditLogServic.save(eLRRAuditLog);
        eLRRAuditLogServic.deleteAll();
    }

}
