package com.conversion.pmk.patient.service;

import com.conversion.pmk.common.enums.CheckinType;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.patient.client.NgemrClient;
import com.conversion.pmk.patient.dto.request.CheckinRequest;
import com.conversion.pmk.patient.dto.response.CheckinResponse;
import com.conversion.pmk.patient.entity.Checkin;
import com.conversion.pmk.patient.entity.Person;
import com.conversion.pmk.patient.entity.Visit;
import com.conversion.pmk.patient.event.PatientCheckinEvent;
import com.conversion.pmk.patient.kafka.PatientEventProducer;
import com.conversion.pmk.patient.mapper.CheckinMapper;
import com.conversion.pmk.patient.repository.CheckinRepository;
import com.conversion.pmk.patient.repository.PersonRepository;
import com.conversion.pmk.patient.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl implements CheckinService {

    private final PersonRepository personRepository;
    private final VisitRepository visitRepository;
    private final CheckinRepository checkinRepository;
    private final NgemrClient ngemrClient;
    private final CheckinMapper checkinMapper;
    private final PatientEventProducer patientEventProducer;

    @Override
    @Transactional
    public CheckinResponse performCheckin(CheckinRequest request) {
        // Validate patient exists
        Person person = personRepository.findByIdRef(request.getIdRef())
                .orElseThrow(() -> new ResourceNotFoundException("Person", request.getIdRef()));

        // Resolve optional linked visit
        Visit visit = null;
        if (request.getVisitId() != null && !request.getVisitId().isBlank()) {
            UUID visitUuid = UUID.fromString(request.getVisitId());
            visit = visitRepository.findById(visitUuid).orElse(null);
        }

        // Submit check-in to NGEMR and retrieve confirmation
        CheckinResponse ngemrResponse = ngemrClient.checkinPerson(request);

        // Persist the check-in transaction record
        Checkin checkin = Checkin.builder()
                .person(person)
                .visit(visit)
                .checkinType(CheckinType.valueOf(request.getCheckinType().toUpperCase()))
                .queueNo(ngemrResponse != null ? ngemrResponse.getQueueNo() : null)
                .locationCode(request.getLocationCode())
                .checkinAt(LocalDateTime.now())
                .build();

        Checkin saved = checkinRepository.save(checkin);
        log.debug("Checkin saved: {} for person idRef={}", saved.getCheckinId(), request.getIdRef());

        // Publish Kafka event for downstream consumers
        PatientCheckinEvent event = PatientCheckinEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .personRef(request.getIdRef())
                .checkinId(saved.getCheckinId().toString())
                .checkinType(saved.getCheckinType().name())
                .locationCode(saved.getLocationCode())
                .checkinAt(saved.getCheckinAt().toString())
                .occurredAt(System.currentTimeMillis())
                .build();

        patientEventProducer.publishCheckin(event);

        CheckinResponse response = checkinMapper.toResponse(saved);
        response.setMessage("Check-in successful");
        return response;
    }
}
