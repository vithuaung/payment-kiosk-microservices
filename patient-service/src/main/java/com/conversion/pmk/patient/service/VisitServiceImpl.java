package com.conversion.pmk.patient.service;

import com.conversion.pmk.common.enums.VisitStatus;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.patient.client.NgemrClient;
import com.conversion.pmk.patient.dto.response.VisitResponse;
import com.conversion.pmk.patient.entity.Person;
import com.conversion.pmk.patient.entity.Visit;
import com.conversion.pmk.patient.mapper.VisitMapper;
import com.conversion.pmk.patient.repository.PersonRepository;
import com.conversion.pmk.patient.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final PersonRepository personRepository;
    private final NgemrClient ngemrClient;
    private final VisitMapper visitMapper;

    @Override
    @Transactional(readOnly = true)
    public List<VisitResponse> getVisits(String idRef, String status) {
        Person person = personRepository.findByIdRef(idRef)
                .orElseThrow(() -> new ResourceNotFoundException("Person", idRef));

        if (status != null && !status.isBlank()) {
            VisitStatus visitStatus = VisitStatus.valueOf(status.toUpperCase());
            return visitRepository.findByPersonPersonIdAndVisitStatus(person.getPersonId(), visitStatus)
                    .stream()
                    .map(visitMapper::toResponse)
                    .collect(Collectors.toList());
        }

        // No status filter — return all visits for the person
        return visitRepository.findAll().stream()
                .filter(v -> v.getPerson().getPersonId().equals(person.getPersonId()))
                .map(visitMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VisitResponse syncFromNgemr(String idRef, UUID visitId) {
        Person person = personRepository.findByIdRef(idRef)
                .orElseThrow(() -> new ResourceNotFoundException("Person", idRef));

        // Fetch visits from NGEMR and find the matching one
        List<VisitResponse> ngemrVisits = ngemrClient.lookupVisits(idRef);
        VisitResponse ngemrVisit = ngemrVisits.stream()
                .filter(v -> visitId.toString().equals(v.getVisitId() != null ? v.getVisitId().toString() : null))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Visit", visitId.toString()));

        // Upsert the visit record locally
        Visit visit = visitRepository.findById(visitId).orElseGet(() -> {
            Visit newVisit = new Visit();
            newVisit.setPerson(person);
            return newVisit;
        });

        visit.setCounterCode(ngemrVisit.getCounterCode());
        visit.setCounterName(ngemrVisit.getCounterName());
        visit.setQueueNo(ngemrVisit.getQueueNo());
        visit.setVisitStatus(ngemrVisit.getVisitStatus() != null
                ? VisitStatus.valueOf(ngemrVisit.getVisitStatus()) : VisitStatus.SCHEDULED);
        visit.setScheduledAt(ngemrVisit.getScheduledAt() != null
                ? LocalDateTime.parse(ngemrVisit.getScheduledAt()) : LocalDateTime.now());

        Visit saved = visitRepository.save(visit);
        log.debug("Synced visit {} from NGEMR for idRef={}", visitId, idRef);
        return visitMapper.toResponse(saved);
    }
}
