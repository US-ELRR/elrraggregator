/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.deloitte.elrr.elrrconsolidate.entity.ContactInformation;
import com.deloitte.elrr.elrrconsolidate.repository.ContactInformationRepository;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContactInformationSvcTest {

    /**
    *
    */
    @Mock
    private ContactInformationRepository mockContactInformationRepository;

    /**
     *
     */
    @Test
    void test() {
        ContactInformationSvc contactInformationSvc = new ContactInformationSvc(
                mockContactInformationRepository);
        ContactInformation contactInformation = new ContactInformation();
        contactInformation.setContactinformationid(1L);

        Mockito.doReturn(contactInformation)
                .when(mockContactInformationRepository).findByEmail("");
        Mockito.doReturn(contactInformation)
                .when(mockContactInformationRepository)
                .save(contactInformation);
        contactInformationSvc.getContactInformationByElectronicmailaddress("");
        contactInformationSvc.getId(contactInformation);
        contactInformationSvc.get(1L);
        contactInformationSvc.save(contactInformation);
    }

}
