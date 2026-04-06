package com.conversion.pmk.payment.service;

import com.conversion.pmk.payment.cache.BillCacheService;
import com.conversion.pmk.payment.client.SapBillingClient;
import com.conversion.pmk.payment.dto.request.BillLookupRequest;
import com.conversion.pmk.payment.dto.response.BillDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {

    private final SapBillingClient sapBillingClient;
    private final BillCacheService billCacheService;

    @Override
    public BillDetailResponse getBillDetails(BillLookupRequest request) {
        // 1. Check cache first
        Optional<BillDetailResponse> cached = billCacheService.getCachedBillDetail(request.getPersonRef());
        if (cached.isPresent()) {
            log.debug("Returning cached bill detail for personRef={}", request.getPersonRef());
            return cached.get();
        }

        // 2. Cache miss — call SAP
        log.debug("Cache miss for personRef={}, calling SAP", request.getPersonRef());
        BillDetailResponse result = sapBillingClient.lookupBills(request.getPersonRef(), request.getOrgCode());

        // 3. Store in cache
        billCacheService.cacheBillDetail(request.getPersonRef(), result);

        return result;
    }
}
