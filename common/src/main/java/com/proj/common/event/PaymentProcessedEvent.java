package com.proj.common.event;

import com.proj.common.dto.BookingRequestDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentProcessedEvent extends BookingEvent {
    private BookingRequestDto.PaymentRequest paymentRequest;
    private String transactionId;
    private String status;
    
    public PaymentProcessedEvent(String bookingId, String userId, BookingRequestDto.PaymentRequest paymentRequest, String transactionId, String status) {
        super(bookingId, userId, "PAYMENT_PROCESSED");
        this.paymentRequest = paymentRequest;
        this.transactionId = transactionId;
        this.status = status;
    }
}
