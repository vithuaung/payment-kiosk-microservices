package com.conversion.pmk.settlement.controller;

import com.conversion.pmk.common.enums.SettlementStatus;
import com.conversion.pmk.common.exception.GlobalExceptionHandler;
import com.conversion.pmk.common.exception.ResourceNotFoundException;
import com.conversion.pmk.settlement.dto.request.SyncSettleRequest;
import com.conversion.pmk.settlement.dto.response.SettlementResponse;
import com.conversion.pmk.settlement.entity.Settlement;
import com.conversion.pmk.settlement.repository.SettlementRepository;
import com.conversion.pmk.settlement.service.SyncSettlementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SettlementController.class)
@Import(GlobalExceptionHandler.class)
class SettlementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SyncSettlementService syncSettlementService;

    @MockBean
    private SettlementRepository settlementRepository;

    @Test
    void syncSettle_validRequest_returns200() throws Exception {
        SyncSettleRequest request = SyncSettleRequest.builder()
                .sessionRef("sess-200")
                .paymentId(UUID.randomUUID())
                .personRef("P-200")
                .payMethod("CASH")
                .billItems(Collections.emptyList())
                .build();

        SettlementResponse response = SettlementResponse.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef("sess-200")
                .settleStatus("DONE")
                .extRef("EXT-200")
                .retryCount(1)
                .build();

        when(syncSettlementService.settle(any(SyncSettleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/settlements/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionRef").value("sess-200"))
                .andExpect(jsonPath("$.data.settleStatus").value("DONE"));
    }

    @Test
    void getSettlement_existing_returns200() throws Exception {
        String sessionRef = "sess-201";

        Settlement settlement = Settlement.builder()
                .settlementId(UUID.randomUUID())
                .sessionRef(sessionRef)
                .settleStatus(SettlementStatus.DONE)
                .extRef("EXT-201")
                .retryCount(1)
                .maxRetry(5)
                .attempts(new ArrayList<>())
                .build();

        when(settlementRepository.findBySessionRef(eq(sessionRef))).thenReturn(Optional.of(settlement));

        mockMvc.perform(get("/api/settlements/{sessionRef}", sessionRef))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionRef").value(sessionRef))
                .andExpect(jsonPath("$.data.settleStatus").value("DONE"));
    }

    @Test
    void getSettlement_notFound_returns404() throws Exception {
        String sessionRef = "sess-999";

        when(settlementRepository.findBySessionRef(eq(sessionRef)))
                .thenThrow(new ResourceNotFoundException("Settlement", sessionRef));

        mockMvc.perform(get("/api/settlements/{sessionRef}", sessionRef))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
