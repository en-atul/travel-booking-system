package com.proj.paymentservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRefundRequest {
    private String bookingId;
    private String userId;
    private String transactionId;
    private String reason;
}
