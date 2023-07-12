package org.mainteny.doman.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.mainteny.enums.JobStatus;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity(name = "Jobs")
@Table(name = "jobs")
public class Jobs {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "job_status_id", nullable = false)
   private Long id;

   @Column(name = "job_status", nullable = false)
   private JobStatus jobStatus;

   @Column(name = "completion_percentage")
   private int completionPercentage;

   @Column(name = "created_date")
   @CreationTimestamp
   private Date createdDate;

   @UpdateTimestamp
   @Column(name = "last_update", insertable = false)
   private Date lastUpdate;
}
