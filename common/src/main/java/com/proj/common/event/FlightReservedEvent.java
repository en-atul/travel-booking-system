package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FlightReservedEvent extends BookingEvent {
    private BookingRequestDto.FlightRequest flightRequest;
    private BookingRequestDto fullBookingRequest;
    private String reservationId;
    
    public FlightReservedEvent(String bookingId, String userId, BookingRequestDto.FlightRequest flightRequest, BookingRequestDto fullBookingRequest, String reservationId) {
        super(bookingId, userId, "FLIGHT_RESERVED");
        this.flightRequest = flightRequest;
        this.fullBookingRequest = fullBookingRequest;
        this.reservationId = reservationId;
    }
}
