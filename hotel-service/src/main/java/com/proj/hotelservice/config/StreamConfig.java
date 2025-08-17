package com.proj.hotelservice.config;

import com.proj.common.event.HotelReservationEvent;
import com.proj.hotelservice.request.HotelCancellationRequest;
import com.proj.hotelservice.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class StreamConfig {

    private final HotelService hotelService;

    @Bean
    public Consumer<HotelReservationEvent> hotelReservationHandler() {
        return event -> {
            log.info("Received hotel reservation event for booking: {}", event.getBookingId());
            
            // Create hotel reservation request from the event
            com.proj.common.request.HotelReservationRequest request = new com.proj.common.request.HotelReservationRequest(
                event.getBookingId(),
                event.getUserId(),
                event.getHotelRequest(),
                event.getFullBookingRequest()
            );
            
            hotelService.reserveHotel(request)
                .subscribe(
                    response -> {
                        log.info("Hotel reservation completed for booking: {}", event.getBookingId());
                        
                        // In Choreography pattern, hotel service decides what to do next
                        // If car is needed, trigger car reservation
                        if (event.getFullBookingRequest().getCar() != null) {
                            com.proj.common.event.CarReservationEvent carEvent = new com.proj.common.event.CarReservationEvent(
                                event.getBookingId(),
                                event.getUserId(),
                                event.getFullBookingRequest().getCar(),
                                event.getFullBookingRequest()
                            );
                            streamBridge.send("car-reservation", carEvent);
                            log.info("Hotel service triggered car reservation for booking: {}", event.getBookingId());
                        } else {
                            // If no car needed, trigger payment directly
                            com.proj.common.event.PaymentEvent paymentEvent = new com.proj.common.event.PaymentEvent(
                                event.getBookingId(),
                                event.getUserId(),
                                event.getFullBookingRequest().getPayment(),
                                event.getFullBookingRequest()
                            );
                            streamBridge.send("payment-processing", paymentEvent);
                            log.info("Hotel service triggered payment processing for booking: {}", event.getBookingId());
                        }
                    },
                    error -> log.error("Hotel reservation failed for booking: {}", event.getBookingId(), error)
                );
        };
    }

    @Bean
    public Consumer<HotelCancellationRequest> hotelCancelHandler() {
        return request -> {
            log.info("Received hotel cancellation request for reservation: {}", request.getReservationId());
            hotelService.cancelHotel(request)
                .subscribe(
                    response -> log.info("Hotel cancellation completed for reservation: {}", request.getReservationId()),
                    error -> log.error("Hotel cancellation failed for reservation: {}", request.getReservationId(), error)
                );
        };
    }
}
