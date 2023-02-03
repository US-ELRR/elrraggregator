/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.deloitte.elrr.elrraggregator.exception.ResourceNotFoundException;
import com.deloitte.elrr.elrrconsolidate.entity.RoleRelations;
import com.deloitte.elrr.elrrconsolidate.repository.RoleRelationsRepository;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleRelationsSvcTest {

    /**
    *
    */
    @Mock
    private RoleRelationsRepository roleRelationsRepository;

    /**
     * @throws ResourceNotFoundException
     *
     */
    @Test
    void test() throws ResourceNotFoundException {
        RoleRelationsSvc roleRelationsSvc = new RoleRelationsSvc(
                roleRelationsRepository);
        RoleRelations roleRelations = new RoleRelations();
        roleRelations.setRolerelationsid(1L);
        List<RoleRelations> roleRelationsList = new ArrayList<>();
        roleRelationsList.add(roleRelations);
        ReflectionTestUtils.setField(roleRelationsSvc,
                "roleRelationsRepository", roleRelationsRepository);
        Mockito.doReturn(roleRelations).when(roleRelationsRepository)
                .save(roleRelations);
        Mockito.doReturn(true).when(roleRelationsRepository).existsById(1L);
        Mockito.doNothing().when(roleRelationsRepository).deleteById(1L);

        roleRelationsSvc.getId(roleRelations);
        roleRelationsSvc.findAll();
        roleRelationsSvc.get(1L);
        roleRelationsSvc.save(roleRelations);
        roleRelationsSvc.deleteAll();
        roleRelationsSvc.delete(1L);
        roleRelationsSvc.update(roleRelations);
        roleRelationsSvc.saveAll(roleRelationsList);
        roleRelations.setRolerelationsid(2L);
        try {
            roleRelationsSvc.update(roleRelations);
        } catch (Exception e) {

        }
    }
}
