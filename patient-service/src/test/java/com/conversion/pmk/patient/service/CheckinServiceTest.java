package com.conversion.pmk.patient.service;

import com.conversion.pmk.common.enums.CheckinType;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.patient.client.NgemrClient;
import com.conversion.pmk.patient.dto.request.CheckinRequest;
import com.conversion.pmk.patient.dto.response.CheckinResponse;
import com.conversion.pmk.patient.entity.Checkin;
import com.conversion.pmk.patient.entity.Person;
import com.conversion.pmk.patient.event.PatientCheckinEvent;
import com.conversion.pmk.patient.kafka.PatientEventProducer;
import com.conversion.pmk.patient.mapper.CheckinMapper;
import com.conversion.pmk.patient.repository.CheckinRepository;
import com.conversion.pmk.patient.repository.PersonRepository;
import com.conversion.pmk.patient.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckinServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private CheckinRepository checkinRepository;

    @Mock
    private NgemrClient ngemrClient;

    @Mock
    private CheckinMapper checkinMapper;

    @Mock
    private PatientEventProducer patientEventProducer;

    @InjectMocks
    private CheckinServiceImpl checkinService;

    private Person samplePerson;
    private CheckinRequest checkinRequest;

    @BeforeEach
    void setup() {
        samplePerson = Person.builder()
                .personId(UUID.randomUUID())
                .idRef("IC123456")
                .fullName("Ahmad bin Abdullah")
                .build();

        checkinRequest = CheckinRequest.builder()
                .idRef("IC123456")
                .checkinType("WALKIN")
                .locationCode("CTR-01")
                .build();
    }

    @Test
    void performCheckin_savesRecordAndPublishesEvent() {
        // Given
        Checkin savedCheckin = Checkin.builder()
                .checkinId(UUID.randomUUID())
                .person(samplePerson)
                .checkinType(CheckinType.WALKIN)
                .locationCode("CTR-01")
                .checkinAt(LocalDateTime.now())
                .build();

        CheckinResponse ngemrResponse = CheckinResponse.builder()
                .queueNo("Q-001")
                .build();

        CheckinResponse mappedResponse = CheckinResponse.builder()
                .checkinId(savedCheckin.getCheckinId().toString())
                .checkinType("WALKIN")
                .locationCode("CTR-01")
                .build();

        when(personRepository.findByIdRef("IC123456")).thenReturn(Optional.of(samplePerson));
        when(ngemrClient.checkinPerson(checkinRequest)).thenReturn(ngemrResponse);
        when(checkinRepository.save(any(Checkin.class))).thenReturn(savedCheckin);
        when(checkinMapper.toResponse(savedCheckin)).thenReturn(mappedResponse);

        // When
        CheckinResponse result = checkinService.performCheckin(checkinRequest);

        // Then
        assertThat(result).isNotNull();
        verify(checkinRepository).save(any(Checkin.class));

        ArgumentCaptor<PatientCheckinEvent> eventCaptor = ArgumentCaptor.forClass(PatientCheckinEvent.class);
        verify(patientEventProducer).publishCheckin(eventCaptor.capture());
        PatientCheckinEvent published = eventCaptor.getValue();
        assertThat(published.getPersonRef()).isEqualTo("IC123456");
        assertThat(published.getCheckinType()).isEqualTo("WALKIN");
        assertThat(published.getLocationCode()).isEqualTo("CTR-01");
    }

    @Test
    void performCheckin_throwsResourceNotFoundException_whenPersonNotFound() {
        // Given
        when(personRepository.findByIdRef("IC000000")).thenReturn(Optional.empty());
        CheckinRequest unknownRequest = CheckinRequest.builder()
                .idRef("IC000000")
                .checkinType("WALKIN")
                .locationCode("CTR-01")
                .build();

        // When / Then
        assertThatThrownBy(() -> checkinService.performCheckin(unknownRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(checkinRepository, never()).save(any());
        verify(patientEventProducer, never()).publishCheckin(any());
    }
}
