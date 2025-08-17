package com.proj.flightservice.controller;

import com.proj.flightservice.dtos.FlightDto;
import com.proj.flightservice.model.Flight;
import com.proj.flightservice.service.FlightService;
import com.proj.common.request.FlightReservationRequest;
import com.proj.flightservice.request.FlightCancellationRequest;
import com.proj.flightservice.response.FlightReservationResponse;
import com.proj.flightservice.response.FlightCancellationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<Flux<FlightDto>> searchFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(defaultValue = "10") int count) {
        
        return ResponseEntity.ok(flightService.searchFlights(origin, destination, count));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<FlightDto>> getFlightById(@PathVariable String id) {
        return ResponseEntity.ok(flightService.getFlightById(id));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Mono<FlightReservationResponse>> reserveFlight(
            @RequestBody FlightReservationRequest request) {
        
        return ResponseEntity.ok(flightService.reserveFlight(request));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Mono<FlightCancellationResponse>> cancelFlight(
            @RequestBody FlightCancellationRequest request) {
        
        return ResponseEntity.ok(flightService.cancelFlight(request));
    }
}
