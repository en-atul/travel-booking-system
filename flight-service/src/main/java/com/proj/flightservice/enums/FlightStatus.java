package com.proj.flightservice.enums;

public enum FlightStatus {
    SCHEDULED,   // flight is planned and open for reservations
    ON_TIME,     // flight is on schedule
    DELAYED,     // departure/arrival delayed
    BOARDING,    // passengers are boarding
    IN_AIR,      // flight is currently flying
    LANDED,      // flight has landed
    CANCELLED,    // flight was cancelled
    AVAILABLE
}

