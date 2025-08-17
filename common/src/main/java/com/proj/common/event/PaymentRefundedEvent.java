package com.proj.common.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentRefundedEvent extends BookingEvent {
    private String transactionId;
    private String refundId;
    private String reason;
    
    public PaymentRefundedEvent(String bookingId, String userId, String transactionId, String refundId, String reason) {
        super(bookingId, userId, "PAYMENT_REFUNDED");
        this.transactionId = transactionId;
        this.refundId = refundId;
        this.reason = reason;
    }
}
