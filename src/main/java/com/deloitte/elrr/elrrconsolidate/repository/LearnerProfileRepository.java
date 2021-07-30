package com.deloitte.elrr.elrrconsolidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.deloitte.elrr.elrrconsolidate.entity.Competency;
import com.deloitte.elrr.elrrconsolidate.entity.LearnerProfile;

@Repository
public interface LearnerProfileRepository  extends JpaRepository<LearnerProfile, Long>{

	//@Query("SELECT u FROM User u WHERE u.status = ?1 and u.name = ?2")
	//List<Long> findUserByStatusAndName(Integer status, String name);

	@Query("SELECT l FROM LearnerProfile l WHERE personid = :personid and courseid = :courseid ")
	public LearnerProfile getLearnerProfileByPersonIdCourseId(final long personid, final long courseid);
}
