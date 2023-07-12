package org.mainteny.service;

import org.mainteny.doman.dto.JobDTO;
import org.mainteny.enums.JobStatus;

import java.util.Optional;
import java.util.Set;

public interface JobService {
   JobDTO createJob();
   boolean updateJobStatus();
   JobDTO startNotStartedJob(Long id);
   Optional<JobDTO> getJobStatus(Long id);
   Set<JobDTO> getAllJobs();
   Set<JobDTO> getJobStatusByStatus(JobStatus status);
}
