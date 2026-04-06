package com.conversion.pmk.mock.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.mock.config.FailureSimulator;
import com.conversion.pmk.mock.dto.ngemr.CheckinRequest;
import com.conversion.pmk.mock.dto.ngemr.CheckinResponse;
import com.conversion.pmk.mock.dto.ngemr.PersonLookupRequest;
import com.conversion.pmk.mock.dto.ngemr.PersonLookupResponse;
import com.conversion.pmk.mock.dto.ngemr.VisitLookupRequest;
import com.conversion.pmk.mock.dto.ngemr.VisitResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

// Simulates the hospital EMR (electronic medical records) endpoints
@Slf4j
@RestController
@RequestMapping("/mock/ngemr")
@RequiredArgsConstructor
public class MockNgemrController {

    private final FailureSimulator failureSimulator;

    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Small set of generic sample persons for deterministic lookups
    private static final Map<String, PersonLookupResponse> SAMPLE_PERSONS = Map.of(
            "P001", PersonLookupResponse.builder()
                    .idRef("P001").fullName("John Sample").birthDate("1980-03-15")
                    .mobileNo("91110001").emailAddr("john.sample@mail.test").build(),
            "P002", PersonLookupResponse.builder()
                    .idRef("P002").fullName("Mary Test").birthDate("1975-07-22")
                    .mobileNo("91110002").emailAddr("mary.test@mail.test").build(),
            "P003", PersonLookupResponse.builder()
                    .idRef("P003").fullName("Alan Demo").birthDate("1990-11-05")
                    .mobileNo("91110003").emailAddr("alan.demo@mail.test").build(),
            "P004", PersonLookupResponse.builder()
                    .idRef("P004").fullName("Susan Mock").birthDate("1965-01-30")
                    .mobileNo("91110004").emailAddr("susan.mock@mail.test").build(),
            "P005", PersonLookupResponse.builder()
                    .idRef("P005").fullName("David Stub").birthDate("2000-09-18")
                    .mobileNo("91110005").emailAddr("david.stub@mail.test").build()
    );

    // Returns patient demographic data; falls back to a dynamic record for unknown IDs
    @PostMapping("/person")
    public ApiResponse<PersonLookupResponse> lookupPerson(@RequestBody PersonLookupRequest request) {
        log.debug("NGEMR person lookup for idRef={}", request.getIdRef());

        if (failureSimulator.shouldFail()) {
            log.warn("NGEMR person lookup simulated failure");
            return ApiResponse.fail("EMR unavailable", "NGEMR_ERROR");
        }

        PersonLookupResponse person = SAMPLE_PERSONS.get(request.getIdRef());
        if (person == null) {
            // Dynamic fallback for IDs not in the fixed set
            String namePrefix = request.getIdRef().length() >= 4
                    ? request.getIdRef().substring(0, 4)
                    : request.getIdRef();
            person = PersonLookupResponse.builder()
                    .idRef(request.getIdRef())
                    .fullName("Patient " + namePrefix)
                    .birthDate("1990-01-01")
                    .mobileNo("90000000")
                    .emailAddr("patient." + namePrefix.toLowerCase() + "@mail.test")
                    .build();
        }

        return ApiResponse.ok(person);
    }

    // Returns 0 to 2 upcoming visits for the patient
    @PostMapping("/visits")
    public ApiResponse<List<VisitResponse>> getVisits(@RequestBody VisitLookupRequest request) {
        log.debug("NGEMR visit lookup for idRef={}", request.getIdRef());

        int count = RANDOM.nextInt(3); // 0, 1, or 2
        List<VisitResponse> visits = new ArrayList<>();

        String[] counterCodes = {"CTR-01", "CTR-02", "CTR-03", "CTR-04"};
        String[] counterNames = {"Counter 1", "Counter 2", "Counter 3", "Counter 4"};

        for (int i = 0; i < count; i++) {
            int slot = RANDOM.nextInt(counterCodes.length);
            String scheduledAt = LocalDateTime.now().plusDays(1 + i).withMinute(0).withSecond(0)
                    .format(DT_FMT);
            visits.add(VisitResponse.builder()
                    .visitId("VIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .scheduledAt(scheduledAt)
                    .counterCode(counterCodes[slot])
                    .counterName(counterNames[slot])
                    .queueNo("Q" + (100 + RANDOM.nextInt(900)))
                    .visitStatus("SCHEDULED")
                    .build());
        }

        return ApiResponse.ok(visits);
    }

    // Checks the patient in and issues a queue number
    @PostMapping("/checkin")
    public ApiResponse<CheckinResponse> checkin(@RequestBody CheckinRequest request) {
        log.debug("NGEMR check-in for idRef={} visitId={}", request.getIdRef(), request.getVisitId());

        if (failureSimulator.shouldFail()) {
            log.warn("NGEMR check-in simulated failure");
            return ApiResponse.fail("EMR check-in failed", "NGEMR_CHECKIN_ERROR");
        }

        int slot = RANDOM.nextInt(4);
        String[] counterNames = {"Counter 1", "Counter 2", "Counter 3", "Counter 4"};
        String queueNo = "Q" + (100 + RANDOM.nextInt(900));

        CheckinResponse response = CheckinResponse.builder()
                .checkinId("CHK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .queueNo(queueNo)
                .counterName(counterNames[slot])
                .message("Check-in successful. Please proceed to " + counterNames[slot])
                .build();

        return ApiResponse.ok(response);
    }
}
