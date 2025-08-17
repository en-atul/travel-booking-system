package com.proj.bookingservice.config;

import com.proj.bookingservice.service.BookingSagaEventHandler;
import com.proj.common.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StreamConfig {

    private final BookingSagaEventHandler sagaEventHandler;

    @Bean
    public Consumer<Message<Object>> reservationEventsHandler() {
        return message -> {
            Object event = message.getPayload();
            log.info("Received reservation event: {}", event.getClass().getSimpleName());
            
            if (event instanceof FlightReservedEvent) {
                sagaEventHandler.handleFlightReserved((FlightReservedEvent) event);
            } else if (event instanceof FlightReservationFailedEvent) {
                sagaEventHandler.handleFlightReservationFailed((FlightReservationFailedEvent) event);
            } else if (event instanceof HotelReservedEvent) {
                sagaEventHandler.handleHotelReserved((HotelReservedEvent) event);
            } else if (event instanceof HotelReservationFailedEvent) {
                sagaEventHandler.handleHotelReservationFailed((HotelReservationFailedEvent) event);
            } else if (event instanceof CarReservedEvent) {
                sagaEventHandler.handleCarReserved((CarReservedEvent) event);
            } else if (event instanceof CarReservationFailedEvent) {
                sagaEventHandler.handleCarReservationFailed((CarReservationFailedEvent) event);
            }
        };
    }

    @Bean
    public Consumer<Message<Object>> paymentEventsHandler() {
        return message -> {
            Object event = message.getPayload();
            log.info("Received payment event: {}", event.getClass().getSimpleName());
            
            if (event instanceof PaymentProcessedEvent) {
                sagaEventHandler.handlePaymentProcessed((PaymentProcessedEvent) event);
            } else if (event instanceof PaymentFailedEvent) {
                sagaEventHandler.handlePaymentFailed((PaymentFailedEvent) event);
            }
        };
    }

    @Bean
    public Consumer<Message<Object>> bookingEventsHandler() {
        return message -> {
            Object event = message.getPayload();
            log.info("Received booking event: {}", event.getClass().getSimpleName());
            
            if (event instanceof BookingCompletedEvent) {
                sagaEventHandler.handleBookingCompleted((BookingCompletedEvent) event);
            }
        };
    }
}
