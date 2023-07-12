package org.mainteny.enums;
public enum JobStatus {
   NOT_STARTED("NOT_STARTED"),
   RUNNING("RUNNING"),
   FAILED("FAILED"),
   FINISHED("FINISHED");

   private String currentStatus;
   JobStatus(String currentStatus) {
      this.currentStatus = currentStatus;
   }
   public String getCurrentStatus() {
      return currentStatus;
   }
}