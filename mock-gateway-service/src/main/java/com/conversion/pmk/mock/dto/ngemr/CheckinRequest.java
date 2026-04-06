package com.conversion.pmk.mock.dto.ngemr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinRequest {

    private String idRef;
    private String visitId;
    private String checkinType;
}
