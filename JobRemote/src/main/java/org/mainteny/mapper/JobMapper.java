package org.mainteny.mapper;

import org.mainteny.doman.dto.JobDTO;
import org.mainteny.doman.entity.Jobs;
import org.mapstruct.Mapper;
@Mapper(componentModel = "spring")
public interface JobMapper {

   JobDTO toJobDTO(Jobs jobs);

   Jobs toJob(JobDTO jobDTO);
}