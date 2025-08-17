package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookingCreatedEvent extends BookingEvent {
    private BookingRequestDto bookingRequest;
    
    public BookingCreatedEvent(String bookingId, String userId, BookingRequestDto bookingRequest) {
        super(bookingId, userId, "BOOKING_CREATED");
        this.bookingRequest = bookingRequest;
    }
}
