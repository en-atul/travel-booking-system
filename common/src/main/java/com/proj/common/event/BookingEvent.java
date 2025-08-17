package com.proj.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BookingEvent {
    private String bookingId;
    private String userId;
    private LocalDateTime timestamp;
    private String eventType;
    
    public BookingEvent(String bookingId, String userId, String eventType) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
    }
}
