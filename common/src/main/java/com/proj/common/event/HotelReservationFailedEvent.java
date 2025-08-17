package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HotelReservationFailedEvent extends BookingEvent {
    private BookingRequestDto.HotelRequest hotelRequest;
    private String errorMessage;
    
    public HotelReservationFailedEvent(String bookingId, String userId, BookingRequestDto.HotelRequest hotelRequest, String errorMessage) {
        super(bookingId, userId, "HOTEL_RESERVATION_FAILED");
        this.hotelRequest = hotelRequest;
        this.errorMessage = errorMessage;
    }
}
