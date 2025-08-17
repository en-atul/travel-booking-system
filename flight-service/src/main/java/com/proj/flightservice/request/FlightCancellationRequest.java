package com.proj.flightservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightCancellationRequest {
    private String bookingId;
    private String userId;
    private String reservationId;
    private String reason;
}
