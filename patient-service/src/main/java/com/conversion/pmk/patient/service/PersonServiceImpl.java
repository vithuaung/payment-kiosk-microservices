package com.conversion.pmk.patient.service;

import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.patient.client.NgemrClient;
import com.conversion.pmk.patient.dto.request.PersonLookupRequest;
import com.conversion.pmk.patient.dto.response.PersonResponse;
import com.conversion.pmk.patient.entity.Person;
import com.conversion.pmk.patient.mapper.PersonMapper;
import com.conversion.pmk.patient.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final NgemrClient ngemrClient;
    private final PersonMapper personMapper;

    @Override
    @Transactional(readOnly = true)
    public PersonResponse findByIdRef(String idRef) {
        return personRepository.findByIdRef(idRef)
                .map(personMapper::toResponse)
                .orElseGet(() -> {
                    log.debug("Person not in DB, fetching from NGEMR for idRef={}", idRef);
                    return fetchFromNgemrAndSave(idRef);
                });
    }

    @Override
    @Transactional
    public PersonResponse register(PersonLookupRequest request) {
        return personRepository.findByIdRef(request.getIdRef())
                .map(existing -> {
                    log.debug("Person already registered, returning existing record for idRef={}", request.getIdRef());
                    return personMapper.toResponse(existing);
                })
                .orElseGet(() -> fetchFromNgemrAndSave(request.getIdRef()));
    }

    // Fetches person data from NGEMR and persists it locally
    @Transactional
    protected PersonResponse fetchFromNgemrAndSave(String idRef) {
        PersonResponse ngemrResponse = ngemrClient.lookupPerson(idRef);
        if (ngemrResponse == null) {
            throw new ResourceNotFoundException("Person", idRef);
        }

        Person person = buildPersonFromResponse(ngemrResponse);
        Person saved = personRepository.save(person);
        log.debug("Saved new person record for idRef={}", idRef);
        return personMapper.toResponse(saved);
    }

    // Constructs a Person entity from an NGEMR lookup response
    private Person buildPersonFromResponse(PersonResponse response) {
        return Person.builder()
                .idRef(response.getIdRef())
                .fullName(response.getFullName())
                .birthDate(response.getBirthDate() != null ? LocalDate.parse(response.getBirthDate()) : null)
                .mobileNo(response.getMobileNo())
                .emailAddr(response.getEmailAddr())
                .build();
    }
}
