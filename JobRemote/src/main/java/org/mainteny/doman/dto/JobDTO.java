package org.mainteny.doman.dto;

import lombok.Getter;
import lombok.Setter;
import org.mainteny.enums.JobStatus;

@Getter
@Setter
public class JobDTO {
   private Long id;
   private JobStatus jobStatus;
   private int completionPercentage;
}
