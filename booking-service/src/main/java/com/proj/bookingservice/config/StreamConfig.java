package com.proj.bookingservice.config;

import com.proj.bookingservice.service.BookingSagaEventHandler;
import com.proj.common.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StreamConfig {

    private final BookingSagaEventHandler sagaEventHandler;

    @Bean
    public Consumer<FlightReservedEvent> flightReservedHandler() {
        return event -> {
            log.info("Received flight reserved event: {}", event.getBookingId());
            sagaEventHandler.handleFlightReserved(event);
        };
    }

    @Bean
    public Consumer<FlightReservationFailedEvent> flightReservationFailedHandler() {
        return event -> {
            log.info("Received flight reservation failed event: {}", event.getBookingId());
            sagaEventHandler.handleFlightReservationFailed(event);
        };
    }

    @Bean
    public Consumer<HotelReservedEvent> hotelReservedHandler() {
        return event -> {
            log.info("Received hotel reserved event: {}", event.getBookingId());
            sagaEventHandler.handleHotelReserved(event);
        };
    }

    @Bean
    public Consumer<HotelReservationFailedEvent> hotelReservationFailedHandler() {
        return event -> {
            log.info("Received hotel reservation failed event: {}", event.getBookingId());
            sagaEventHandler.handleHotelReservationFailed(event);
        };
    }

    @Bean
    public Consumer<CarReservedEvent> carReservedHandler() {
        return event -> {
            log.info("Received car reserved event: {}", event.getBookingId());
            sagaEventHandler.handleCarReserved(event);
        };
    }

    @Bean
    public Consumer<CarReservationFailedEvent> carReservationFailedHandler() {
        return event -> {
            log.info("Received car reservation failed event: {}", event.getBookingId());
            sagaEventHandler.handleCarReservationFailed(event);
        };
    }

    @Bean
    public Consumer<PaymentProcessedEvent> paymentProcessedHandler() {
        return event -> {
            log.info("Received payment processed event: {}", event.getBookingId());
            sagaEventHandler.handlePaymentProcessed(event);
        };
    }

    @Bean
    public Consumer<PaymentFailedEvent> paymentFailedHandler() {
        return event -> {
            log.info("Received payment failed event: {}", event.getBookingId());
            sagaEventHandler.handlePaymentFailed(event);
        };
    }

    @Bean
    public Consumer<BookingCompletedEvent> bookingCompletedHandler() {
        return event -> {
            log.info("Received booking completed event for booking: {}", event.getBookingId());
            sagaEventHandler.handleBookingCompleted(event);
        };
    }
}
