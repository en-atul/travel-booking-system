package com.proj.paymentservice.config;

import com.proj.common.event.PaymentEvent;
import com.proj.paymentservice.request.PaymentRefundRequest;
import com.proj.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StreamConfig {

    private final PaymentService paymentService;

    @Bean
    public Consumer<PaymentEvent> paymentProcessingHandler() {
        return event -> {
            log.info("Received payment processing event for booking: {}", event.getBookingId());
            
            // Create payment request from the event
            com.proj.common.request.PaymentRequest request = new com.proj.common.request.PaymentRequest(
                event.getBookingId(),
                event.getUserId(),
                event.getPaymentRequest(),
                event.getFullBookingRequest()
            );
            
            paymentService.processPayment(request)
                .subscribe(
                    response -> {
                        log.info("Payment processing completed for booking: {}", event.getBookingId());
                        
                        // In Choreography pattern, payment service completes the booking
                        // Publish booking completed event
                        com.proj.common.event.BookingCompletedEvent bookingCompletedEvent = new com.proj.common.event.BookingCompletedEvent(
                            event.getBookingId(),
                            event.getUserId(),
                            null, // flightReservationId - would be set by booking service
                            null, // hotelReservationId - would be set by booking service
                            null, // carReservationId - would be set by booking service
                            response.getTransactionId() // paymentTransactionId
                        );
                        streamBridge.send("booking-completed", bookingCompletedEvent);
                        log.info("Payment service completed booking: {}", event.getBookingId());
                    },
                    error -> log.error("Payment processing failed for booking: {}", event.getBookingId(), error)
                );
        };
    }

    @Bean
    public Consumer<PaymentRefundRequest> paymentRefundHandler() {
        return request -> {
            log.info("Received payment refund request for transaction: {}", request.getTransactionId());
            paymentService.refundPayment(request)
                .subscribe(
                    response -> log.info("Payment refund completed for transaction: {}", request.getTransactionId()),
                    error -> log.error("Payment refund failed for transaction: {}", request.getTransactionId(), error)
                );
        };
    }
}
