package com.conversion.pmk.patient.mapper;

import com.conversion.pmk.patient.dto.request.PersonLookupRequest;
import com.conversion.pmk.patient.dto.response.PersonResponse;
import com.conversion.pmk.patient.entity.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Maps between Person entity and related DTOs
@Mapper(componentModel = "spring")
public interface PersonMapper {

    // Maps idRef from request; other fields are populated by service/NGEMR
    @Mapping(target = "personId", ignore = true)
    @Mapping(target = "fullName", ignore = true)
    @Mapping(target = "birthDate", ignore = true)
    @Mapping(target = "mobileNo", ignore = true)
    @Mapping(target = "emailAddr", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Person toEntity(PersonLookupRequest request);

    // Maps birthDate LocalDate to ISO string
    @Mapping(target = "birthDate", expression = "java(person.getBirthDate() != null ? person.getBirthDate().toString() : null)")
    PersonResponse toResponse(Person person);
}
