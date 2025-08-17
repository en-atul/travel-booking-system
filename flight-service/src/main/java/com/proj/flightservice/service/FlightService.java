package com.proj.flightservice.service;

import com.proj.flightservice.dtos.FlightDto;
import com.proj.flightservice.enums.FlightStatus;
import com.proj.flightservice.enums.FlightReservationStatus;
import com.proj.flightservice.model.FlightReservation;
import com.proj.flightservice.repository.FlightReservationRepository;
import com.proj.flightservice.request.FlightCancellationRequest;
import com.proj.common.request.FlightReservationRequest;
import com.proj.flightservice.response.FlightCancellationResponse;
import com.proj.flightservice.response.FlightReservationResponse;
import com.proj.common.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {

    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final StreamBridge streamBridge;
    private final FlightReservationRepository flightReservationRepository;

    public Flux<FlightDto> searchFlights(String origin, String destination, int count) {
        return Flux.fromStream(
                IntStream.range(0, count).mapToObj(i -> {
                    String flightNumber = faker.aviation().flight();
                    String airline = faker.aviation().airline();
                    
                    // Use provided origin/destination or generate random ones
                    String flightOrigin = (origin != null) ? origin : faker.aviation().airport();
                    String flightDestination = (destination != null) ? destination : faker.aviation().airport();
                    
                    LocalDateTime departure = LocalDateTime.now().plusHours(random.nextInt(24));
                    LocalDateTime arrival = departure.plusHours(random.nextInt(5) + 1);
                    FlightStatus status = randomStatus();

                    return new FlightDto(flightNumber, airline, flightOrigin, flightDestination, departure, arrival, status);
                })
        );
    }

    public Mono<FlightDto> getFlightById(String id) {
        // Mock implementation - in real scenario, fetch from database
        return Mono.just(new FlightDto(
            "FL" + id, 
            faker.aviation().airline(), 
            faker.aviation().airport(), 
            faker.aviation().airport(), 
            LocalDateTime.now().plusHours(2), 
            LocalDateTime.now().plusHours(4), 
            FlightStatus.AVAILABLE
        ));
    }

    public Mono<FlightReservationResponse> reserveFlight(FlightReservationRequest request) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Starting flight reservation for booking: {}", request.getBookingId());
                
                // Check if reservation already exists
                if (flightReservationRepository.findByBookingId(request.getBookingId()).isPresent()) {
                    log.warn("Flight reservation already exists for booking: {}", request.getBookingId());
                    throw new RuntimeException("Flight reservation already exists for this booking");
                }
                
                // Create flight reservation record
                FlightReservation reservation = new FlightReservation();
                reservation.setBookingId(request.getBookingId());
                reservation.setUserId(request.getUserId());
                reservation.setFlightId(request.getFlightRequest().getFlightId());
                reservation.setDeparture(request.getFlightRequest().getDeparture());
                reservation.setArrival(request.getFlightRequest().getArrival());
                reservation.setFlightDate(request.getFlightRequest().getDate().atStartOfDay());
                reservation.setStatus(FlightReservationStatus.PENDING);
                
                // Convert passenger details to string list for storage
                List<String> passengerDetails = request.getFlightRequest().getPassengerDetails().stream()
                    .map(passenger -> String.format("%s %s - Seat: %s", 
                        passenger.getFirstName(), 
                        passenger.getLastName(), 
                        passenger.getSeat()))
                    .collect(Collectors.toList());
                reservation.setPassengerDetails(passengerDetails);
                
                // Save reservation to database
                reservation = flightReservationRepository.save(reservation);
                log.info("Flight reservation saved with ID: {}", reservation.getId());
                
                // Simulate flight availability check (in real scenario, check actual flight availability)
                if (isFlightAvailable(request.getFlightRequest())) {
                    // Update status to reserved
                    reservation.setStatus(FlightReservationStatus.RESERVED);
                    reservation = flightReservationRepository.save(reservation);
                    
                    // Publish flight reserved event
                    FlightReservedEvent event = new FlightReservedEvent(
                        request.getBookingId(),
                        request.getUserId(),
                        request.getFlightRequest(),
                        request.getFullBookingRequest(),
                        reservation.getId()
                    );
                    
                    streamBridge.send("reservation-events", event);
                    log.info("Flight reserved successfully for booking: {} with reservation ID: {}", 
                        request.getBookingId(), reservation.getId());
                    
                    return new FlightReservationResponse(
                        request.getBookingId(),
                        reservation.getId(),
                        "RESERVED",
                        "Flight reserved successfully"
                    );
                } else {
                    // Flight not available
                    reservation.setStatus(FlightReservationStatus.FAILED);
                    reservation.setErrorMessage("Flight not available");
                    flightReservationRepository.save(reservation);
                    
                    throw new RuntimeException("Flight not available");
                }
                
            } catch (Exception e) {
                log.error("Flight reservation failed for booking: {}", request.getBookingId(), e);
                
                // Save failed reservation if not already saved
                try {
                    FlightReservation failedReservation = new FlightReservation();
                    failedReservation.setBookingId(request.getBookingId());
                    failedReservation.setUserId(request.getUserId());
                    failedReservation.setFlightId(request.getFlightRequest().getFlightId());
                    failedReservation.setDeparture(request.getFlightRequest().getDeparture());
                    failedReservation.setArrival(request.getFlightRequest().getArrival());
                    failedReservation.setFlightDate(request.getFlightRequest().getDate().atStartOfDay());
                    failedReservation.setStatus(FlightReservationStatus.FAILED);
                    failedReservation.setErrorMessage(e.getMessage());
                    flightReservationRepository.save(failedReservation);
                } catch (Exception saveException) {
                    log.error("Failed to save failed reservation", saveException);
                }
                
                // Publish flight reservation failed event
                FlightReservationFailedEvent event = new FlightReservationFailedEvent(
                    request.getBookingId(),
                    request.getUserId(),
                    request.getFlightRequest(),
                    e.getMessage()
                );
                
                streamBridge.send("reservation-events", event);
                
                throw new RuntimeException("Flight reservation failed: " + e.getMessage());
            }
        });
    }

    public Mono<FlightCancellationResponse> cancelFlight(FlightCancellationRequest request) {
        return Mono.fromCallable(() -> {
            try {
                log.info("Starting flight cancellation for reservation: {}", request.getReservationId());
                
                // Find the reservation
                FlightReservation reservation = flightReservationRepository.findById(request.getReservationId())
                    .orElseThrow(() -> new RuntimeException("Flight reservation not found: " + request.getReservationId()));
                
                // Check if reservation can be cancelled
                if (reservation.getStatus() == FlightReservationStatus.CANCELLED) {
                    log.warn("Flight reservation already cancelled: {}", request.getReservationId());
                    return new FlightCancellationResponse(
                        request.getBookingId(),
                        request.getReservationId(),
                        "ALREADY_CANCELLED",
                        "Flight reservation already cancelled"
                    );
                }
                
                if (reservation.getStatus() == FlightReservationStatus.FAILED) {
                    log.warn("Flight reservation already failed: {}", request.getReservationId());
                    return new FlightCancellationResponse(
                        request.getBookingId(),
                        request.getReservationId(),
                        "ALREADY_FAILED",
                        "Flight reservation already failed"
                    );
                }
                
                // Update status to cancelled
                reservation.setStatus(FlightReservationStatus.CANCELLED);
                flightReservationRepository.save(reservation);
                
                log.info("Flight cancelled successfully for reservation: {}", request.getReservationId());
                
                return new FlightCancellationResponse(
                    request.getBookingId(),
                    request.getReservationId(),
                    "CANCELLED",
                    "Flight cancelled successfully"
                );
                
            } catch (Exception e) {
                log.error("Flight cancellation failed for reservation: {}", request.getReservationId(), e);
                throw new RuntimeException("Flight cancellation failed: " + e.getMessage());
            }
        });
    }

    // Legacy method for backward compatibility
    public Flux<FlightDto> getFlights(int count) {
        return searchFlights(null, null, count);
    }

    // Legacy method for backward compatibility
    public Flux<FlightDto> searchFlightsByOrigin(String origin, int count) {
        return searchFlights(origin, null, count);
    }

    // Legacy method for backward compatibility
    public Flux<FlightDto> searchFlightsByOriginAndDestination(String origin, String destination, int count) {
        return searchFlights(origin, destination, count);
    }

    private FlightStatus randomStatus() {
        FlightStatus[] statuses = FlightStatus.values();
        return statuses[random.nextInt(statuses.length)];
    }

    private boolean isFlightAvailable(com.proj.common.dto.BookingRequestDto.FlightRequest flightRequest) {
        // Simulate flight availability check
        // In real scenario, this would check against actual flight inventory
        // For now, we'll simulate 90% success rate
        return random.nextDouble() > 0.1; // 90% chance of availability
    }
}
