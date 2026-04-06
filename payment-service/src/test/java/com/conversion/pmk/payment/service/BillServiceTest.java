package com.conversion.pmk.payment.service;

import com.conversion.pmk.common.exception.PmkException;
import com.conversion.pmk.payment.cache.BillCacheService;
import com.conversion.pmk.payment.client.SapBillingClient;
import com.conversion.pmk.payment.dto.request.BillLookupRequest;
import com.conversion.pmk.payment.dto.response.BillDetailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private SapBillingClient sapBillingClient;

    @Mock
    private BillCacheService billCacheService;

    @InjectMocks
    private BillServiceImpl billService;

    private BillLookupRequest request;
    private BillDetailResponse sampleResponse;

    @BeforeEach
    void setUp() {
        request = BillLookupRequest.builder()
                .personRef("P-001")
                .orgCode("ORG-01")
                .build();

        sampleResponse = BillDetailResponse.builder()
                .personRef("P-001")
                .personName("John Doe")
                .totalPayable(BigDecimal.valueOf(150.00))
                .build();
    }

    @Test
    void cacheHit_returnsCachedResult() {
        // Arrange
        when(billCacheService.getCachedBillDetail("P-001"))
                .thenReturn(Optional.of(sampleResponse));

        // Act
        BillDetailResponse result = billService.getBillDetails(request);

        // Assert
        assertThat(result).isEqualTo(sampleResponse);
        verify(sapBillingClient, never()).lookupBills(anyString(), anyString());
        verify(billCacheService, never()).cacheBillDetail(anyString(), any());
    }

    @Test
    void cacheMiss_callsSapAndCaches() {
        // Arrange
        when(billCacheService.getCachedBillDetail("P-001"))
                .thenReturn(Optional.empty());
        when(sapBillingClient.lookupBills("P-001", "ORG-01"))
                .thenReturn(sampleResponse);

        // Act
        BillDetailResponse result = billService.getBillDetails(request);

        // Assert
        assertThat(result).isEqualTo(sampleResponse);
        verify(sapBillingClient).lookupBills("P-001", "ORG-01");
        verify(billCacheService).cacheBillDetail("P-001", sampleResponse);
    }

    @Test
    void sapError_throwsPmkException() {
        // Arrange
        when(billCacheService.getCachedBillDetail("P-001"))
                .thenReturn(Optional.empty());
        when(sapBillingClient.lookupBills("P-001", "ORG-01"))
                .thenThrow(new PmkException("Billing service unavailable", "SAP_UNAVAILABLE"));

        // Act & Assert
        assertThatThrownBy(() -> billService.getBillDetails(request))
                .isInstanceOf(PmkException.class)
                .hasMessageContaining("Billing service unavailable");
    }
}
