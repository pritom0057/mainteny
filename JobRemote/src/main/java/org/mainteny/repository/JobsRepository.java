
package org.mainteny.repository;

import org.mainteny.doman.entity.Jobs;
import org.mainteny.enums.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JobsRepository extends JpaRepository<Jobs, Long> {
   Optional<Jobs> findFirstByJobStatusOrderByIdAsc(JobStatus jobStatus);
   Set<Jobs> findByJobStatus(JobStatus jobStatus);
   Set<Jobs> findByLastUpdateLessThanAndJobStatus(Date timestamp, JobStatus jobStatus);
   Set<Jobs> findByLastUpdateGreaterThan(Date timestamp);

   @Transactional
   @Modifying
   @Query("UPDATE Jobs j SET j.jobStatus = :jobStatus WHERE j IN :jobs")
   void updateJobStatusForSetOfJobs(Set<Jobs> jobs, JobStatus jobStatus);

   @Modifying
   @Query("UPDATE Jobs j SET j.jobStatus = :jobStatus WHERE j = :job")
   @Transactional
   void updateJobStatusForSingleJob(@Param("job") Jobs job, @Param("jobStatus") JobStatus jobStatus);

   @Modifying
   @Query("UPDATE Jobs j SET j.completionPercentage = :completionPercentage WHERE j = :job")
   @Transactional
   void updateCompletionPercentage(Jobs job, int completionPercentage);

   @Modifying
   @Query("UPDATE Jobs j SET j.completionPercentage = :completionPercentage, j.jobStatus = :jobStatus WHERE j.id = :jobId")
   @Transactional
   void updateCompletionPercentageAndStatus(Long jobId, int completionPercentage, JobStatus jobStatus);

   @Modifying
   @Query("UPDATE Jobs j SET j.completionPercentage = :completionPercentage, j.jobStatus = :jobStatus WHERE j IN :jobs")
   @Transactional
   void updateCompletionPercentageAndStatusAll(Set<Jobs> jobs, int completionPercentage, JobStatus jobStatus);
}
