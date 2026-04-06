package com.conversion.pmk.mock;

import com.conversion.pmk.mock.config.FailureSimulator;
import com.conversion.pmk.mock.controller.MockSapController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockSapController.class)
class MockSapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FailureSimulator failureSimulator;

    // SAP bill-details should return success and bill data when no failure is triggered
    @Test
    void billDetails_returnsOk_whenNoFailure() throws Exception {
        when(failureSimulator.shouldFail()).thenReturn(false);

        String body = objectMapper.writeValueAsString(
                Map.of("personRef", "P001", "orgCode", "ORG-01"));

        mockMvc.perform(post("/mock/sap/bill-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.personRef").value("P001"))
                .andExpect(jsonPath("$.data.bills").isArray());
    }

    // SAP bill-details should return an error payload when the simulator triggers a failure
    @Test
    void billDetails_returnsError_whenFailureSimulated() throws Exception {
        when(failureSimulator.shouldFail()).thenReturn(true);

        String body = objectMapper.writeValueAsString(
                Map.of("personRef", "P001", "orgCode", "ORG-01"));

        mockMvc.perform(post("/mock/sap/bill-details")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("SAP_ERROR"));
    }

    // SAP bill-post should return POSTED status when no failure is triggered
    @Test
    void billPost_returnsPosted_whenNoFailure() throws Exception {
        when(failureSimulator.shouldFail()).thenReturn(false);

        String body = objectMapper.writeValueAsString(Map.of(
                "sessionRef", "SES-001",
                "personRef", "P001",
                "items", java.util.List.of(
                        Map.of("billRef", "BILL-001", "billSeq", 1, "payableAmt", "120.00")),
                "payMethod", "NETS"));

        mockMvc.perform(post("/mock/sap/bill-post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.postStatus").value("POSTED"))
                .andExpect(jsonPath("$.data.extRef").value(org.hamcrest.Matchers.startsWith("SAP-")));
    }
}
