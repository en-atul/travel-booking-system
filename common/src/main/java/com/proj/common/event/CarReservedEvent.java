package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CarReservedEvent extends BookingEvent {
    private BookingRequestDto.CarRequest carRequest;
    private BookingRequestDto fullBookingRequest;
    private String reservationId;
    
    public CarReservedEvent(String bookingId, String userId, BookingRequestDto.CarRequest carRequest, BookingRequestDto fullBookingRequest, String reservationId) {
        super(bookingId, userId, "CAR_RESERVED");
        this.carRequest = carRequest;
        this.fullBookingRequest = fullBookingRequest;
        this.reservationId = reservationId;
    }
}
