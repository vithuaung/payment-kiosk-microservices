package com.conversion.pmk.patient.controller;

import com.conversion.pmk.common.dto.ApiResponse;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.patient.dto.request.PersonLookupRequest;
import com.conversion.pmk.patient.dto.response.PersonResponse;
import com.conversion.pmk.patient.service.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonService personService;

    @Test
    void lookup_returns200WithPersonData_whenIdRefIsValid() throws Exception {
        // Given
        PersonLookupRequest request = new PersonLookupRequest("IC123456");
        PersonResponse response = PersonResponse.builder()
                .personId(UUID.randomUUID())
                .idRef("IC123456")
                .fullName("Ahmad bin Abdullah")
                .build();

        when(personService.findByIdRef("IC123456")).thenReturn(response);

        // When / Then
        mockMvc.perform(post("/api/patients/lookup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.idRef").value("IC123456"))
                .andExpect(jsonPath("$.data.fullName").value("Ahmad bin Abdullah"));
    }

    @Test
    void lookup_returns400_whenIdRefIsBlank() throws Exception {
        // Given — blank idRef should fail @NotBlank validation
        PersonLookupRequest request = new PersonLookupRequest("");

        // When / Then
        mockMvc.perform(post("/api/patients/lookup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lookup_returns404_whenPersonNotFound() throws Exception {
        // Given
        PersonLookupRequest request = new PersonLookupRequest("IC000000");
        when(personService.findByIdRef("IC000000"))
                .thenThrow(new ResourceNotFoundException("Person", "IC000000"));

        // When / Then
        mockMvc.perform(post("/api/patients/lookup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
