package com.proj.flightservice.config;

import com.proj.common.event.BookingCreatedEvent;
import com.proj.flightservice.request.FlightCancellationRequest;
import com.proj.flightservice.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StreamConfig {

    private final FlightService flightService;

    @Bean
    public Consumer<BookingCreatedEvent> bookingCreatedHandler(StreamBridge streamBridge) {
        return event -> {
            log.info("Received booking created event for booking: {}", event.getBookingId());
            
            // Create flight reservation request from the booking event
            com.proj.common.request.FlightReservationRequest request = new com.proj.common.request.FlightReservationRequest(
                event.getBookingId(),
                event.getUserId(),
                event.getBookingRequest().getFlight(),
                event.getBookingRequest()
            );
            
            flightService.reserveFlight(request)
                .subscribe(
                    response -> {
                        log.info("Flight reservation completed for booking: {}", event.getBookingId());
                        
                        // In Choreography pattern, flight service decides what to do next
                        // If hotel is needed, trigger hotel reservation
                        if (event.getBookingRequest().getHotel() != null) {
                            com.proj.common.event.HotelReservationEvent hotelEvent = new com.proj.common.event.HotelReservationEvent(
                                event.getBookingId(),
                                event.getUserId(),
                                event.getBookingRequest().getHotel(),
                                event.getBookingRequest()
                            );
                            streamBridge.send("hotel-reservation", hotelEvent);
                            log.info("Flight service triggered hotel reservation for booking: {}", event.getBookingId());
                        } else if (event.getBookingRequest().getCar() != null) {
                            // If no hotel but car is needed, trigger car reservation
                            com.proj.common.event.CarReservationEvent carEvent = new com.proj.common.event.CarReservationEvent(
                                event.getBookingId(),
                                event.getUserId(),
                                event.getBookingRequest().getCar(),
                                event.getBookingRequest()
                            );
                            streamBridge.send("car-reservation", carEvent);
                            log.info("Flight service triggered car reservation for booking: {}", event.getBookingId());
                        } else {
                            // If no hotel or car, trigger payment directly
                            com.proj.common.event.PaymentEvent paymentEvent = new com.proj.common.event.PaymentEvent(
                                event.getBookingId(),
                                event.getUserId(),
                                event.getBookingRequest().getPayment(),
                                event.getBookingRequest()
                            );
                            streamBridge.send("payment-processing", paymentEvent);
                            log.info("Flight service triggered payment processing for booking: {}", event.getBookingId());
                        }
                    },
                    error -> log.error("Flight reservation failed for booking: {}", event.getBookingId(), error)
                );
        };
    }

    @Bean
    public Consumer<FlightCancellationRequest> flightCancelHandler() {
        return request -> {
            log.info("Received flight cancellation request for reservation: {}", request.getReservationId());
            flightService.cancelFlight(request)
                .subscribe(
                    response -> log.info("Flight cancellation completed for reservation: {}", request.getReservationId()),
                    error -> log.error("Flight cancellation failed for reservation: {}", request.getReservationId(), error)
                );
        };
    }
}
