package com.proj.paymentservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String bookingId;
    private String transactionId;
    private String status;
    private String message;
}
