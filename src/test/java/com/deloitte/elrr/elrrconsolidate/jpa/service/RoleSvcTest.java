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
import com.deloitte.elrr.elrrconsolidate.entity.Role;
import com.deloitte.elrr.elrrconsolidate.repository.RoleRepository;

/**
 * @author mnelakurti
 *
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleSvcTest {

    /**
    *
    */
    @Mock
    private RoleRepository roleRepository;

    /**
     * @throws ResourceNotFoundException
     *
     */
    @Test
    void test() throws ResourceNotFoundException {
        RoleSvc roleSvc = new RoleSvc(roleRepository);
        Role role = new Role();
        role.setRoleid(1L);
        List<Role> roleList = new ArrayList<>();
        roleList.add(role);
        ReflectionTestUtils.setField(roleSvc,
                "roleRepository", roleRepository);
        Mockito.doReturn(role).when(roleRepository).save(role);
        Mockito.doReturn(true).when(roleRepository).existsById(1L);
        Mockito.doNothing().when(roleRepository).deleteById(1L);

        roleSvc.getId(role);
        roleSvc.findAll();
        roleSvc.get(1L);
        roleSvc.save(role);
        roleSvc.deleteAll();
        roleSvc.delete(1L);
        roleSvc.update(role);
        roleSvc.saveAll(roleList);
        role.setRoleid(2L);
        try {
            roleSvc.update(role);
        } catch (Exception e) {

        }
    }
}
