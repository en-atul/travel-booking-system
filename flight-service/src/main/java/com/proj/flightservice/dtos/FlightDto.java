package com.proj.flightservice.dtos;

import com.proj.flightservice.enums.FlightStatus;

import java.time.LocalDateTime;

public record FlightDto(
        String flightNumber,
        String airline,
        String origin,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        FlightStatus status
) {
}
