package com.conversion.pmk.patient.mapper;

import com.conversion.pmk.patient.dto.response.VisitResponse;
import com.conversion.pmk.patient.entity.Visit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Maps between Visit entity and response DTO
@Mapper(componentModel = "spring")
public interface VisitMapper {

    @Mapping(target = "scheduledAt", expression = "java(visit.getScheduledAt() != null ? visit.getScheduledAt().toString() : null)")
    @Mapping(target = "visitStatus", expression = "java(visit.getVisitStatus() != null ? visit.getVisitStatus().name() : null)")
    VisitResponse toResponse(Visit visit);
}
