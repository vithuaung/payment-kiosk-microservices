package com.conversion.pmk.mock.dto.ngemr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinResponse {

    private String checkinId;
    private String queueNo;
    private String counterName;
    private String message;
}
