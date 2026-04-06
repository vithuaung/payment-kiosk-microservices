package com.conversion.pmk.patient.mapper;

import com.conversion.pmk.patient.dto.response.CheckinResponse;
import com.conversion.pmk.patient.entity.Checkin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Maps between Checkin entity and response DTO
@Mapper(componentModel = "spring")
public interface CheckinMapper {

    @Mapping(target = "checkinType", expression = "java(checkin.getCheckinType() != null ? checkin.getCheckinType().name() : null)")
    @Mapping(target = "checkinAt", expression = "java(checkin.getCheckinAt() != null ? checkin.getCheckinAt().toString() : null)")
    @Mapping(target = "message", ignore = true)
    CheckinResponse toResponse(Checkin checkin);
}
