package com.conversion.pmk.payment.service;

import com.conversion.pmk.payment.dto.request.CashSessionRequest;
import com.conversion.pmk.payment.dto.response.CashSessionResponse;

import java.math.BigDecimal;

public interface CashSessionService {

    CashSessionResponse openSession(String sessionRef);

    CashSessionResponse updateSession(CashSessionRequest request);

    CashSessionResponse closeSession(String sessionRef, BigDecimal insertedAmt, BigDecimal returnedAmt);
}
