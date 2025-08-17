package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FlightReservationFailedEvent extends BookingEvent {
    private BookingRequestDto.FlightRequest flightRequest;
    private String errorMessage;
    
    public FlightReservationFailedEvent(String bookingId, String userId, BookingRequestDto.FlightRequest flightRequest, String errorMessage) {
        super(bookingId, userId, "FLIGHT_RESERVATION_FAILED");
        this.flightRequest = flightRequest;
        this.errorMessage = errorMessage;
    }
}
