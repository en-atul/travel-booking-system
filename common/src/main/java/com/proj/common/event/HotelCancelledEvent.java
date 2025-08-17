package com.proj.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HotelCancelledEvent extends BookingEvent {
    private String reservationId;
    private String reason;
    
    public HotelCancelledEvent(String bookingId, String userId, String reservationId, String reason) {
        super(bookingId, userId, "HOTEL_CANCELLED");
        this.reservationId = reservationId;
        this.reason = reason;
    }
}
