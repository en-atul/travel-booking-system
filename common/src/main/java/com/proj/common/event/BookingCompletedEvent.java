package com.proj.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookingCompletedEvent extends BookingEvent {
    private String flightReservationId;
    private String hotelReservationId;
    private String carReservationId;
    private String paymentTransactionId;
    
    public BookingCompletedEvent(String bookingId, String userId, String flightReservationId, String hotelReservationId, String carReservationId, String paymentTransactionId) {
        super(bookingId, userId, "BOOKING_COMPLETED");
        this.flightReservationId = flightReservationId;
        this.hotelReservationId = hotelReservationId;
        this.carReservationId = carReservationId;
        this.paymentTransactionId = paymentTransactionId;
    }
}
