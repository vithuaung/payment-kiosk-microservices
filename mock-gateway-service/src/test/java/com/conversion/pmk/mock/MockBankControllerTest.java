package com.conversion.pmk.mock;

import com.conversion.pmk.mock.config.FailureSimulator;
import com.conversion.pmk.mock.controller.MockBankController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MockBankController.class)
class MockBankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FailureSimulator failureSimulator;

    // NETS initiate should return APPROVED and a NETS- prefixed txnRef when no failure
    @Test
    void netsInitiate_returnsApproved_whenNoFailure() throws Exception {
        when(failureSimulator.shouldFail()).thenReturn(false);

        String body = objectMapper.writeValueAsString(
                Map.of("sessionRef", "SES-001", "amount", "150.00", "terminalCode", "TRM-01"));

        mockMvc.perform(post("/mock/bank/nets/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.txnRef").value(org.hamcrest.Matchers.startsWith("NETS-")));
    }

    // NETS initiate should return DECLINED when the simulator triggers a failure
    @Test
    void netsInitiate_returnsDeclined_whenFailureSimulated() throws Exception {
        when(failureSimulator.shouldFail()).thenReturn(true);

        String body = objectMapper.writeValueAsString(
                Map.of("sessionRef", "SES-002", "amount", "200.00", "terminalCode", "TRM-01"));

        mockMvc.perform(post("/mock/bank/nets/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DECLINED"))
                .andExpect(jsonPath("$.data.message").value("Insufficient funds"));
    }

    // Card charge should return APPROVED and a CRD- prefixed approvalRef when no failure
    @Test
    void cardCharge_returnsApproved_whenNoFailure() throws Exception {
        when(failureSimulator.shouldFail()).thenReturn(false);

        String body = objectMapper.writeValueAsString(Map.of(
                "sessionRef", "SES-003",
                "amount", "75.50",
                "cardNetwork", "VISA",
                "terminalCode", "TRM-01"));

        mockMvc.perform(post("/mock/bank/card/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"))
                .andExpect(jsonPath("$.data.approvalRef").value(org.hamcrest.Matchers.startsWith("CRD-")))
                .andExpect(jsonPath("$.data.cardNetwork").value("VISA"));
    }

    // Transaction status query for a NETS- prefixed reference should return SETTLED
    @Test
    void transactionStatus_returnsSettled_forNetsPrefix() throws Exception {
        mockMvc.perform(get("/mock/bank/transaction/NETS-ABCD1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SETTLED"))
                .andExpect(jsonPath("$.data.txnRef").value("NETS-ABCD1234"));
    }

    // Transaction status query for a CRD- prefixed reference should also return SETTLED
    @Test
    void transactionStatus_returnsSettled_forCardPrefix() throws Exception {
        mockMvc.perform(get("/mock/bank/transaction/CRD-XYZ00001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("SETTLED"));
    }

    // Transaction status query for an unknown reference should return NOT_FOUND
    @Test
    void transactionStatus_returnsNotFound_forUnknownRef() throws Exception {
        mockMvc.perform(get("/mock/bank/transaction/UNKNOWN-REF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("NOT_FOUND"));
    }
}
