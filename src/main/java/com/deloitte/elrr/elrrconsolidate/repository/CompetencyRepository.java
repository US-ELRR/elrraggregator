package com.deloitte.elrr.elrrconsolidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.deloitte.elrr.elrrconsolidate.entity.Competency;

@Repository
public interface CompetencyRepository extends JpaRepository<Competency, Long> {

    /**
     * @param competencyframeworktitle
     * @return Competency competency
     */

    @Query("SELECT c FROM Competency c WHERE LOWER(c.competencyframeworktitle)"
            + " = LOWER(:competencyframeworktitle)")
    Competency findByCompetencyName(String competencyframeworktitle);

}
