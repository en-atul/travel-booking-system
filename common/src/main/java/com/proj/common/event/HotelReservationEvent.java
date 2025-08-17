package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HotelReservationEvent extends BookingEvent {
    private BookingRequestDto.HotelRequest hotelRequest;
    private BookingRequestDto fullBookingRequest;
    
    public HotelReservationEvent(String bookingId, String userId, BookingRequestDto.HotelRequest hotelRequest, BookingRequestDto fullBookingRequest) {
        super(bookingId, userId, "HOTEL_RESERVATION");
        this.hotelRequest = hotelRequest;
        this.fullBookingRequest = fullBookingRequest;
    }
}
