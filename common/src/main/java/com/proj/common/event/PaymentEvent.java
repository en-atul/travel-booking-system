package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentEvent extends BookingEvent {
    private BookingRequestDto.PaymentRequest paymentRequest;
    private BookingRequestDto fullBookingRequest;
    
    public PaymentEvent(String bookingId, String userId, BookingRequestDto.PaymentRequest paymentRequest, BookingRequestDto fullBookingRequest) {
        super(bookingId, userId, "PAYMENT_PROCESSING");
        this.paymentRequest = paymentRequest;
        this.fullBookingRequest = fullBookingRequest;
    }
}
