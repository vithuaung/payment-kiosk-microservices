package com.conversion.pmk.patient.service;

import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.patient.client.NgemrClient;
import com.conversion.pmk.patient.dto.request.PersonLookupRequest;
import com.conversion.pmk.patient.dto.response.PersonResponse;
import com.conversion.pmk.patient.entity.Person;
import com.conversion.pmk.patient.kafka.PatientEventProducer;
import com.conversion.pmk.patient.mapper.PersonMapper;
import com.conversion.pmk.patient.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private NgemrClient ngemrClient;

    @Mock
    private PersonMapper personMapper;

    @Mock
    private PatientEventProducer patientEventProducer;

    @InjectMocks
    private PersonServiceImpl personService;

    private Person samplePerson;
    private PersonResponse sampleResponse;

    @BeforeEach
    void setup() {
        samplePerson = Person.builder()
                .personId(UUID.randomUUID())
                .idRef("IC123456")
                .fullName("Ahmad bin Abdullah")
                .build();

        sampleResponse = PersonResponse.builder()
                .personId(samplePerson.getPersonId())
                .idRef("IC123456")
                .fullName("Ahmad bin Abdullah")
                .build();
    }

    @Test
    void findByIdRef_returnsFromDb_whenPersonExists() {
        // Given
        when(personRepository.findByIdRef("IC123456")).thenReturn(Optional.of(samplePerson));
        when(personMapper.toResponse(samplePerson)).thenReturn(sampleResponse);

        // When
        PersonResponse result = personService.findByIdRef("IC123456");

        // Then
        assertThat(result.getIdRef()).isEqualTo("IC123456");
        verify(ngemrClient, never()).lookupPerson(anyString());
    }

    @Test
    void findByIdRef_callsNgemrAndSaves_whenPersonNotInDb() {
        // Given
        when(personRepository.findByIdRef("IC999999")).thenReturn(Optional.empty());
        PersonResponse ngemrPerson = PersonResponse.builder()
                .idRef("IC999999")
                .fullName("Nurul binti Hassan")
                .build();
        when(ngemrClient.lookupPerson("IC999999")).thenReturn(ngemrPerson);
        when(personRepository.save(any(Person.class))).thenReturn(samplePerson);
        when(personMapper.toResponse(any(Person.class))).thenReturn(ngemrPerson);

        // When
        PersonResponse result = personService.findByIdRef("IC999999");

        // Then
        assertThat(result).isNotNull();
        verify(ngemrClient).lookupPerson("IC999999");
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void findByIdRef_throwsResourceNotFoundException_whenNgemrReturnsNull() {
        // Given
        when(personRepository.findByIdRef("IC000000")).thenReturn(Optional.empty());
        when(ngemrClient.lookupPerson("IC000000")).thenReturn(null);

        // When / Then
        assertThatThrownBy(() -> personService.findByIdRef("IC000000"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void register_savesNewPerson_fromNgemrResponse() {
        // Given
        PersonLookupRequest request = new PersonLookupRequest("IC888888");
        when(personRepository.findByIdRef("IC888888")).thenReturn(Optional.empty());
        PersonResponse ngemrPerson = PersonResponse.builder()
                .idRef("IC888888")
                .fullName("Rajesh Kumar")
                .build();
        when(ngemrClient.lookupPerson("IC888888")).thenReturn(ngemrPerson);
        when(personRepository.save(any(Person.class))).thenReturn(samplePerson);
        when(personMapper.toResponse(any(Person.class))).thenReturn(ngemrPerson);

        // When
        PersonResponse result = personService.register(request);

        // Then
        assertThat(result).isNotNull();
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void register_returnsExistingPerson_whenAlreadyRegistered() {
        // Given
        PersonLookupRequest request = new PersonLookupRequest("IC123456");
        when(personRepository.findByIdRef("IC123456")).thenReturn(Optional.of(samplePerson));
        when(personMapper.toResponse(samplePerson)).thenReturn(sampleResponse);

        // When
        PersonResponse result = personService.register(request);

        // Then
        assertThat(result.getIdRef()).isEqualTo("IC123456");
        verify(ngemrClient, never()).lookupPerson(anyString());
    }
}
