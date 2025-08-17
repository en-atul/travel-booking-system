package com.proj.flightservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightReservationResponse {
    private String bookingId;
    private String reservationId;
    private String status;
    private String message;
}
