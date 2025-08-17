package com.proj.carrentalservice.config;

import com.proj.common.event.CarReservationEvent;
import com.proj.carrentalservice.request.CarCancellationRequest;
import com.proj.carrentalservice.service.CarRentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StreamConfig {

    private final CarRentalService carRentalService;

    @Bean
    public Consumer<CarReservationEvent> carReservationHandler() {
        return event -> {
            log.info("Received car reservation event for booking: {}", event.getBookingId());
            
            // Create car reservation request from the event
            com.proj.common.request.CarReservationRequest request = new com.proj.common.request.CarReservationRequest(
                event.getBookingId(),
                event.getUserId(),
                event.getCarRequest(),
                event.getFullBookingRequest()
            );
            
            carRentalService.reserveCar(request)
                .subscribe(
                    response -> {
                        log.info("Car reservation completed for booking: {}", event.getBookingId());
                        
                        // In Choreography pattern, car service decides what to do next
                        // Always trigger payment processing after car reservation
                        com.proj.common.event.PaymentEvent paymentEvent = new com.proj.common.event.PaymentEvent(
                            event.getBookingId(),
                            event.getUserId(),
                            event.getFullBookingRequest().getPayment(),
                            event.getFullBookingRequest()
                        );
                        streamBridge.send("payment-processing", paymentEvent);
                        log.info("Car service triggered payment processing for booking: {}", event.getBookingId());
                    },
                    error -> log.error("Car reservation failed for booking: {}", event.getBookingId(), error)
                );
        };
    }

    @Bean
    public Consumer<CarCancellationRequest> carCancelHandler() {
        return request -> {
            log.info("Received car cancellation request for reservation: {}", request.getReservationId());
            carRentalService.cancelCar(request)
                .subscribe(
                    response -> log.info("Car cancellation completed for reservation: {}", request.getReservationId()),
                    error -> log.error("Car cancellation failed for reservation: {}", request.getReservationId(), error)
                );
        };
    }
}
