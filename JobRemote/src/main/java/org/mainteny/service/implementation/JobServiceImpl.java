package org.mainteny.service.implementation;

import lombok.RequiredArgsConstructor;
import org.mainteny.doman.dto.JobDTO;
import org.mainteny.doman.entity.Jobs;
import org.mainteny.enums.JobStatus;
import org.mainteny.mapper.JobMapper;
import org.mainteny.repository.JobsRepository;
import org.mainteny.service.JobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JobServiceImpl implements JobService {

   private final ExecutorService executor;
   private final JobsRepository jobsRepository;
   private final JobMapper jobMapper;

   @Value("${job.finish.time}")
   private Long jobFinishTime;
   @Value("${job.finish.step}")
   private int jobFinishStep;

   @Override
   public JobDTO createJob() {
      var jobs = new Jobs();
      jobs.setJobStatus(JobStatus.NOT_STARTED);
      return jobMapper.toJobDTO(jobsRepository.save(jobs));
   }

   @Override
   public boolean updateJobStatus() {
      // Create a Date instance representing the timestamp
      var timestamp = new Date(System.currentTimeMillis() - (jobFinishTime * (100 / jobFinishStep)));
      var jobs = jobsRepository.findByLastUpdateLessThanAndJobStatus(timestamp, JobStatus.RUNNING);
      jobsRepository.updateCompletionPercentageAndStatusAll(jobs, 100, JobStatus.FINISHED);
      return true;
   }

   @Override
   public JobDTO startNotStartedJob(Long id) {
      var jobs = jobsRepository.findById(id);
      if (jobs.isPresent()) {
         if (jobs.get().getJobStatus() != JobStatus.NOT_STARTED && jobs.get().getJobStatus() != JobStatus.FAILED) {
            throw new IllegalArgumentException("The job id status is not NOT_STARTED or FAILED");
         }
         jobs.get().setJobStatus(JobStatus.RUNNING);
         jobsRepository.save(jobs.get());
         // start an independent job
         executor.execute(() -> startJob(id));
         return jobMapper.toJobDTO(jobs.get());
      }
      else {
         throw new IllegalArgumentException("The job id not found in the DB");
      }
   }

   @Override
   public Optional<JobDTO> getJobStatus(Long id) {
      var jobs = jobsRepository.findById(id);
      return jobs.map(jobMapper::toJobDTO);
   }

   @Override
   public Set<JobDTO> getAllJobs() {
      return jobsRepository.findAll().stream().map(jobMapper::toJobDTO).collect(Collectors.toSet());
   }

   @Override
   public Set<JobDTO> getJobStatusByStatus(JobStatus status) {
      return jobsRepository.findByJobStatus(status).stream().map(jobMapper::toJobDTO).collect(Collectors.toSet());
   }

   public void startJob(Long id) {
      var isJobDone = false;
      var job = jobsRepository.findById(id).orElseThrow();
      try {
         while (!isJobDone) {
            var currentTimeInMS = System.currentTimeMillis();
            if (job.getLastUpdate() == null) {
               job.setLastUpdate(new Date(currentTimeInMS));
            }
            if ((currentTimeInMS - job.getLastUpdate().getTime()) > jobFinishTime) {
               if (job.getCompletionPercentage() >= (100 - jobFinishStep)) {
                  jobsRepository.updateCompletionPercentageAndStatus(job.getId(), 100, JobStatus.FINISHED);
                  isJobDone = true;
                  continue;
               }
               jobsRepository.updateCompletionPercentage(job, job.getCompletionPercentage() + jobFinishStep);
               job.setLastUpdate(new Date(currentTimeInMS));
               job.setCompletionPercentage(job.getCompletionPercentage() + jobFinishStep);
            }
         }
      }
      catch (Exception e) {
         // In case of an exception, the job should be in the failed state
         jobsRepository.updateJobStatusForSingleJob(job, JobStatus.FAILED);
      }
   }

}
