package com.proj.hotelservice.service;

import com.proj.common.request.HotelReservationRequest;
import com.proj.hotelservice.request.HotelCancellationRequest;
import com.proj.hotelservice.response.HotelCancellationResponse;
import com.proj.hotelservice.response.HotelReservationResponse;
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
public class HotelService {

    private final Random random = new Random();
    private final StreamBridge streamBridge;

    public Mono<HotelReservationResponse> reserveHotel(HotelReservationRequest request) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Starting hotel reservation for booking: {}", request.getBookingId());
                
                // Simulate hotel availability check
                if (isHotelAvailable(request.getHotelRequest())) {
                    String reservationId = UUID.randomUUID().toString();
                    
                    // Publish hotel reserved event
                    HotelReservedEvent event = new HotelReservedEvent(
                        request.getBookingId(),
                        request.getUserId(),
                        request.getHotelRequest(),
                        request.getFullBookingRequest(),
                        reservationId
                    );
                    
                    streamBridge.send("reservation-events", event);
                    log.info("Hotel reserved successfully for booking: {} with reservation ID: {}", 
                        request.getBookingId(), reservationId);
                    
                    return new HotelReservationResponse(
                        request.getBookingId(),
                        reservationId,
                        "RESERVED",
                        "Hotel reserved successfully"
                    );
                } else {
                    // Hotel not available
                    HotelReservationFailedEvent event = new HotelReservationFailedEvent(
                        request.getBookingId(),
                        request.getUserId(),
                        request.getHotelRequest(),
                        "Hotel not available"
                    );
                    
                    streamBridge.send("reservation-events", event);
                    throw new RuntimeException("Hotel not available");
                }
                
            } catch (Exception e) {
                log.error("Hotel reservation failed for booking: {}", request.getBookingId(), e);
                
                // Publish hotel reservation failed event
                HotelReservationFailedEvent event = new HotelReservationFailedEvent(
                    request.getBookingId(),
                    request.getUserId(),
                    request.getHotelRequest(),
                    e.getMessage()
                );
                
                streamBridge.send("reservation-events", event);
                
                throw new RuntimeException("Hotel reservation failed: " + e.getMessage());
            }
        });
    }

    public Mono<HotelCancellationResponse> cancelHotel(HotelCancellationRequest request) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Starting hotel cancellation for reservation: {}", request.getReservationId());
                
                // Simulate hotel cancellation
                log.info("Hotel cancelled successfully for reservation: {}", request.getReservationId());
                
                return new HotelCancellationResponse(
                    request.getBookingId(),
                    request.getReservationId(),
                    "CANCELLED",
                    "Hotel cancelled successfully"
                );
                
            } catch (Exception e) {
                log.error("Hotel cancellation failed for reservation: {}", request.getReservationId(), e);
                throw new RuntimeException("Hotel cancellation failed: " + e.getMessage());
            }
        });
    }

    private boolean isHotelAvailable(com.proj.common.dto.BookingRequestDto.HotelRequest hotelRequest) {
        // Simulate hotel availability check
        // In real scenario, this would check against actual hotel inventory
        // For now, we'll simulate 90% success rate
        return random.nextDouble() > 0.1; // 90% chance of availability
    }
}
