package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CarReservationFailedEvent extends BookingEvent {
    private BookingRequestDto.CarRequest carRequest;
    private String errorMessage;
    
    public CarReservationFailedEvent(String bookingId, String userId, BookingRequestDto.CarRequest carRequest, String errorMessage) {
        super(bookingId, userId, "CAR_RESERVATION_FAILED");
        this.carRequest = carRequest;
        this.errorMessage = errorMessage;
    }
}
