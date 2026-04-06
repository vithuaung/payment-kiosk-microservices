package com.conversion.pmk.payment.service;

import com.conversion.pmk.payment.dto.request.CardSessionRequest;
import com.conversion.pmk.payment.dto.response.CardSessionResponse;

public interface CardSessionService {

    CardSessionResponse openSession(String sessionRef);

    CardSessionResponse updateSession(CardSessionRequest request);
}
