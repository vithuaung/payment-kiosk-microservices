package com.conversion.pmk.mock.dto.ngemr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitResponse {

    private String visitId;
    private String scheduledAt;
    private String counterCode;
    private String counterName;
    private String queueNo;
    private String visitStatus;
}
