package org.mainteny.controller;

import lombok.RequiredArgsConstructor;
import org.mainteny.doman.dto.JobDTO;
import org.mainteny.enums.JobStatus;
import org.mainteny.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/job")
public class JobController {

   private final JobService jobService;

   @GetMapping
   public ResponseEntity<Set<JobDTO>> getAllJobs() {
      Set<JobDTO> jobs = jobService.getAllJobs();
      return Optional.ofNullable(jobs).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
   }

   @PostMapping("/start/{id}")
   public ResponseEntity<JobDTO> startRandomJob(@PathVariable Long id) {
      JobDTO startedJob = jobService.startNotStartedJob(id);
      return Optional.ofNullable(startedJob).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
   }

   @GetMapping("/status/{id}")
   public ResponseEntity<JobDTO> getJobStatusById(@PathVariable Long id) {
      return jobService.getJobStatus(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
   }


   @GetMapping("/{status}")
   public ResponseEntity<Set<JobDTO>> getJobStatusByStatus(@PathVariable JobStatus status) {
      Set<JobDTO> jobs = jobService.getJobStatusByStatus(status);
      return Optional.ofNullable(jobs).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());   }

}
