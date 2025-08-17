package com.proj.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookingFailedEvent extends BookingEvent {
    private String failureReason;
    private String failedStep;
    
    public BookingFailedEvent(String bookingId, String userId, String failureReason, String failedStep) {
        super(bookingId, userId, "BOOKING_FAILED");
        this.failureReason = failureReason;
        this.failedStep = failedStep;
    }
}
