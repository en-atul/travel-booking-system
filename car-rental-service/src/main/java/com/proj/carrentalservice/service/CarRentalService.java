package com.proj.carrentalservice.service;

import com.proj.common.request.CarReservationRequest;
import com.proj.carrentalservice.request.CarCancellationRequest;
import com.proj.carrentalservice.response.CarCancellationResponse;
import com.proj.carrentalservice.response.CarReservationResponse;
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
public class CarRentalService {

    private final Random random = new Random();
    private final StreamBridge streamBridge;

    public Mono<CarReservationResponse> reserveCar(CarReservationRequest request) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Starting car reservation for booking: {}", request.getBookingId());
                
                // Simulate car availability check
                if (isCarAvailable(request.getCarRequest())) {
                    String reservationId = UUID.randomUUID().toString();
                    
                    // Publish car reserved event
                    CarReservedEvent event = new CarReservedEvent(
                        request.getBookingId(),
                        request.getUserId(),
                        request.getCarRequest(),
                        request.getFullBookingRequest(),
                        reservationId
                    );
                    
                    streamBridge.send("reservation-events", event);
                    log.info("Car reserved successfully for booking: {} with reservation ID: {}", 
                        request.getBookingId(), reservationId);
                    
                    return new CarReservationResponse(
                        request.getBookingId(),
                        reservationId,
                        "RESERVED",
                        "Car reserved successfully"
                    );
                } else {
                    // Car not available
                    CarReservationFailedEvent event = new CarReservationFailedEvent(
                        request.getBookingId(),
                        request.getUserId(),
                        request.getCarRequest(),
                        "Car not available"
                    );
                    
                    streamBridge.send("reservation-events", event);
                    throw new RuntimeException("Car not available");
                }
                
            } catch (Exception e) {
                log.error("Car reservation failed for booking: {}", request.getBookingId(), e);
                
                // Publish car reservation failed event
                CarReservationFailedEvent event = new CarReservationFailedEvent(
                    request.getBookingId(),
                    request.getUserId(),
                    request.getCarRequest(),
                    e.getMessage()
                );
                
                streamBridge.send("reservation-events", event);
                
                throw new RuntimeException("Car reservation failed: " + e.getMessage());
            }
        });
    }

    public Mono<CarCancellationResponse> cancelCar(CarCancellationRequest request) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Starting car cancellation for reservation: {}", request.getReservationId());
                
                // Simulate car cancellation
                log.info("Car cancelled successfully for reservation: {}", request.getReservationId());
                
                return new CarCancellationResponse(
                    request.getBookingId(),
                    request.getReservationId(),
                    "CANCELLED",
                    "Car cancelled successfully"
                );
                
            } catch (Exception e) {
                log.error("Car cancellation failed for reservation: {}", request.getReservationId(), e);
                throw new RuntimeException("Car cancellation failed: " + e.getMessage());
            }
        });
    }

    private boolean isCarAvailable(com.proj.common.dto.BookingRequestDto.CarRequest carRequest) {
        // Simulate car availability check
        // In real scenario, this would check against actual car inventory
        // For now, we'll simulate 90% success rate
        return random.nextDouble() > 0.1; // 90% chance of availability
    }
}
