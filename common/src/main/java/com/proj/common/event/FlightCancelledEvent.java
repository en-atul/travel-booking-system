package com.proj.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FlightCancelledEvent extends BookingEvent {
    private String reservationId;
    private String reason;
    
    public FlightCancelledEvent(String bookingId, String userId, String reservationId, String reason) {
        super(bookingId, userId, "FLIGHT_CANCELLED");
        this.reservationId = reservationId;
        this.reason = reason;
    }
}
