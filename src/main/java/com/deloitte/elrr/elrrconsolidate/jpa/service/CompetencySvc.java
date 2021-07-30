/**
 * 
 */
package com.deloitte.elrr.elrrconsolidate.jpa.service;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import com.deloitte.elrr.elrrconsolidate.entity.Competency;
import com.deloitte.elrr.elrrconsolidate.repository.CompetencyRepository;


/**
 * @author mnelakurti
 *
 */

@Service
public class CompetencySvc implements CommonSvc<Competency, Long> {
	
	private final CompetencyRepository competencyRepository;

	public CompetencySvc(final CompetencyRepository competencyRepository) {
		this.competencyRepository = competencyRepository;
	}

	public Competency findByCompetencyName(String name) {
		return competencyRepository.findByCompetencyName(name);
	}
	
	@Override
	public CrudRepository<Competency, Long> getRepository() {
		return this.competencyRepository;
	}

	@Override
	public Long getId(Competency competency) {
		return competency.getCompetencyid();
	}

	@Override
	public Competency save(Competency competency) {
		return CommonSvc.super.save(competency);
	}

}
