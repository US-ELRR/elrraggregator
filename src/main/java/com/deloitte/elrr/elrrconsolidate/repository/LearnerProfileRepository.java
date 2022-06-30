package com.deloitte.elrr.elrrconsolidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.deloitte.elrr.elrrconsolidate.entity.LearnerProfile;

@Repository
public interface LearnerProfileRepository
        extends JpaRepository<LearnerProfile, Long> {

    /**
     *
     * @param personid
     * @param courseid
     * @return LearnerProfile learnerProfile
     */
    @Query("SELECT l FROM LearnerProfile l WHERE personid "
         + "= :personid and courseid = :courseid ")
    LearnerProfile getLearnerProfileByPersonIdCourseId(
            long personid, long courseid);
}
