package org.mainteny;

import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.Unirest;
import org.mainteny.enums.JobStatus;
import org.mainteny.model.JobDTO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class JobManager {

   private static final Logger logger = Logger.getLogger(JobManager.class.getName());
   private static final int NUMBER_OF_JOB = 100;
   private static final String START_JOB_URL = "http://localhost:8080/api/v1/job/start/";
   private static final String GET_STATUS_URL = "http://localhost:8080/api/v1/job/status/";
   private static final int STATUS_API_CALL_INTERVAL = 1000;

   public static void startJob(Long jobId) {

      ExecutorService executorStart = Executors.newSingleThreadExecutor();
      executorStart.execute(() -> {
         try {
            var startResponse = Unirest.post(START_JOB_URL + jobId).asJson();

            if (startResponse.getStatus() == 200) {
               var gson = new GsonBuilder().create();
               var jobDTO = gson.fromJson(startResponse.getBody().toString(), JobDTO.class);
               logger.info(String.format("Job started with ID: %d", jobDTO.getId()));
            }
            else {
               logger.warning(String.format("Job ID %d Failed to start the job: %s", jobId, startResponse.getStatusText()));
            }
         }
         catch (Exception e) {
            logger.warning(String.format("Job ID %d Failed to start the job: %s", jobId, e.getMessage()));
         }
      });
      executorStart.shutdown();
   }

   public static void getJobStatusUntilDone(Long jobId) {
      ExecutorService executorStatus = Executors.newSingleThreadExecutor();
      executorStatus.execute(() -> {
         var isJobDone = false;
         while (!isJobDone) {
            try {
               var statusResponse = Unirest.get(GET_STATUS_URL + jobId).asJson();
               if (statusResponse.getStatus() == 200) {
                  // Process the job status response
                  var gson = new GsonBuilder().create();
                  var jobDTO = gson.fromJson(statusResponse.getBody().toString(), JobDTO.class);
                  logger.info(String.format("Job ID %d Job status: %s & %d%% completed.", jobId, jobDTO.getJobStatus(), jobDTO.getCompletionPercentage()));

                  // Check if the job status has changed
                  if (jobDTO.getJobStatus() == JobStatus.FINISHED) {
                     isJobDone = true;
                     logger.info(String.format("Job ID %d %d%% completed.", jobId, jobDTO.getCompletionPercentage()));
                  }
                  else {
                     // Sleep for a certain duration before making the next status check
                     try {
                        Thread.sleep(STATUS_API_CALL_INTERVAL); // Sleep for 1 second (adjust as needed)
                     }
                     catch (InterruptedException e) {
                        logger.warning(String.format("Job ID %d Insomnia....", jobId));
                     }
                  }
               }
               else {
                  // Handle error response from the status API call
                  logger.warning(String.format("Job ID %d Failed to retrieve job status: %s", jobId, statusResponse.getStatusText()));
                  break;
               }
            }
            catch (Exception e) {
               logger.warning(String.format("Job ID %d Failed to job status: %s", jobId, e.getMessage()));
            }
         }
      });
      executorStatus.shutdown();
   }

   public static void main(String[] args) {
      logger.info("*********STARTED************");
      // Submit all the startJob tasks
      for (long i = 1; i <= NUMBER_OF_JOB; i++) {
         startJob(i);
      }
      for (long i = 1; i <= NUMBER_OF_JOB; i++) {
         getJobStatusUntilDone(i);
      }

   }
}
