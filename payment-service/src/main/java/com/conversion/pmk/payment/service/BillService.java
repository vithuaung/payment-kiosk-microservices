package com.conversion.pmk.payment.service;

import com.conversion.pmk.payment.dto.request.BillLookupRequest;
import com.conversion.pmk.payment.dto.response.BillDetailResponse;

public interface BillService {

    BillDetailResponse getBillDetails(BillLookupRequest request);
}
