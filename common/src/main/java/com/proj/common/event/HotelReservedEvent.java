package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HotelReservedEvent extends BookingEvent {
    private BookingRequestDto.HotelRequest hotelRequest;
    private BookingRequestDto fullBookingRequest;
    private String reservationId;
    
    public HotelReservedEvent(String bookingId, String userId, BookingRequestDto.HotelRequest hotelRequest, BookingRequestDto fullBookingRequest, String reservationId) {
        super(bookingId, userId, "HOTEL_RESERVED");
        this.hotelRequest = hotelRequest;
        this.fullBookingRequest = fullBookingRequest;
        this.reservationId = reservationId;
    }
}
