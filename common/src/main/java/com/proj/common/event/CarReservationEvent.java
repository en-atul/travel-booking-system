package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CarReservationEvent extends BookingEvent {
    private BookingRequestDto.CarRequest carRequest;
    private BookingRequestDto fullBookingRequest;
    
    public CarReservationEvent(String bookingId, String userId, BookingRequestDto.CarRequest carRequest, BookingRequestDto fullBookingRequest) {
        super(bookingId, userId, "CAR_RESERVATION");
        this.carRequest = carRequest;
        this.fullBookingRequest = fullBookingRequest;
    }
}
