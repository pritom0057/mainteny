package org.mainteny.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mainteny.service.JobService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JobScheduler {

   private final JobService jobService;
   @Scheduled(fixedDelay = 5000)
   public void createNewJob() {
      jobService.createJob();
   }

   //schedule check if some job is not yet finished
   @Scheduled(fixedDelay = 10000)
   public void updateOngoingJobStatus() {
      jobService.updateJobStatus();
   }
}
