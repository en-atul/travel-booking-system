package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentFailedEvent extends BookingEvent {
    private BookingRequestDto.PaymentRequest paymentRequest;
    private String errorMessage;
    
    public PaymentFailedEvent(String bookingId, String userId, BookingRequestDto.PaymentRequest paymentRequest, String errorMessage) {
        super(bookingId, userId, "PAYMENT_FAILED");
        this.paymentRequest = paymentRequest;
        this.errorMessage = errorMessage;
    }
}
