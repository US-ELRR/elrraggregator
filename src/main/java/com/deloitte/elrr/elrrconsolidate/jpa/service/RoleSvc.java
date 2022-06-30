/**
 *
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.Role;
import com.deloitte.elrr.elrrconsolidate.repository.RoleRepository;

/**
 * @author mnelakurti
 *
 */

@Service
public class RoleSvc implements CommonSvc<Role, Long> {

    /**
     *
     */
    private final RoleRepository roleRepository;

    /**
     *
     * @param newRoleRepository
     */
    public RoleSvc(final RoleRepository newRoleRepository) {
        this.roleRepository = newRoleRepository;
    }

    /**
     *
     */
    @Override
    public CrudRepository<Role, Long> getRepository() {
        return this.roleRepository;
    }

    /**
     *
     */
    @Override
    public Long getId(final Role role) {
        return role.getRoleid();
    }

    /**
     *
     */
    @Override
    public Role save(final Role role) {
        return CommonSvc.super.save(role);
    }

}
