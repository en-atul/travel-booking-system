package com.proj.paymentservice.service;

import com.proj.common.request.PaymentRequest;
import com.proj.paymentservice.request.PaymentRefundRequest;
import com.proj.paymentservice.response.PaymentRefundResponse;
import com.proj.paymentservice.response.PaymentResponse;
import com.proj.common.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final Random random = new Random();
    private final StreamBridge streamBridge;

    public Mono<PaymentResponse> processPayment(PaymentRequest request) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Starting payment processing for booking: {}", request.getBookingId());
                
                // Simulate payment processing
                if (isPaymentSuccessful(request.getPaymentRequest())) {
                    String transactionId = UUID.randomUUID().toString();
                    
                    // Publish payment processed event
                    PaymentProcessedEvent event = new PaymentProcessedEvent(
                        request.getBookingId(),
                        request.getUserId(),
                        request.getPaymentRequest(),
                        transactionId
                    );
                    
                    streamBridge.send("payment-events", event);
                    log.info("Payment processed successfully for booking: {} with transaction ID: {}", 
                        request.getBookingId(), transactionId);
                    
                    return new PaymentResponse(
                        request.getBookingId(),
                        transactionId,
                        "PROCESSED",
                        "Payment processed successfully"
                    );
                } else {
                    // Payment failed
                    PaymentFailedEvent event = new PaymentFailedEvent(
                        request.getBookingId(),
                        request.getUserId(),
                        request.getPaymentRequest(),
                        "Payment failed"
                    );
                    
                    streamBridge.send("payment-events", event);
                    throw new RuntimeException("Payment failed");
                }
                
            } catch (Exception e) {
                log.error("Payment processing failed for booking: {}", request.getBookingId(), e);
                
                // Publish payment failed event
                PaymentFailedEvent event = new PaymentFailedEvent(
                    request.getBookingId(),
                    request.getUserId(),
                    request.getPaymentRequest(),
                    e.getMessage()
                );
                
                streamBridge.send("payment-events", event);
                
                throw new RuntimeException("Payment processing failed: " + e.getMessage());
            }
        });
    }

    public Mono<PaymentRefundResponse> refundPayment(PaymentRefundRequest request) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Starting payment refund for transaction: {}", request.getTransactionId());
                
                // Simulate payment refund
                log.info("Payment refunded successfully for transaction: {}", request.getTransactionId());
                
                return new PaymentRefundResponse(
                    request.getBookingId(),
                    request.getTransactionId(),
                    "REFUNDED",
                    "Payment refunded successfully"
                );
                
            } catch (Exception e) {
                log.error("Payment refund failed for transaction: {}", request.getTransactionId(), e);
                throw new RuntimeException("Payment refund failed: " + e.getMessage());
            }
        });
    }

    private boolean isPaymentSuccessful(com.proj.common.dto.BookingRequestDto.PaymentRequest paymentRequest) {
        // Simulate payment processing
        // In real scenario, this would integrate with payment gateway
        // For now, we'll simulate 95% success rate
        return random.nextDouble() > 0.05; // 95% chance of success
    }
}
