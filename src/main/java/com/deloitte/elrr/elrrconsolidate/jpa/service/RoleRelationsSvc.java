/**
 * 
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.RoleRelations;
import com.deloitte.elrr.elrrconsolidate.repository.RoleRelationsRepository;

/**
 * @author mnelakurti
 *
 */

@Service
public class RoleRelationsSvc implements CommonSvc<RoleRelations, Long> {
	private final RoleRelationsRepository roleRelationsRepository;

	public RoleRelationsSvc(final RoleRelationsRepository roleRelationsRepository) {
		this.roleRelationsRepository = roleRelationsRepository;
	}

	@Override
	public CrudRepository<RoleRelations, Long> getRepository() {
		return this.roleRelationsRepository;
	}

	@Override
	public Long getId(RoleRelations role) {
		return role.getRolerelationsid();
	}

	@Override
	public RoleRelations save(RoleRelations role) {
		return CommonSvc.super.save(role);
	}

}
