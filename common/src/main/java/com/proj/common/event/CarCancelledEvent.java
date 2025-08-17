package com.proj.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CarCancelledEvent extends BookingEvent {
    private String reservationId;
    private String reason;
    
    public CarCancelledEvent(String bookingId, String userId, String reservationId, String reason) {
        super(bookingId, userId, "CAR_CANCELLED");
        this.reservationId = reservationId;
        this.reason = reason;
    }
}
