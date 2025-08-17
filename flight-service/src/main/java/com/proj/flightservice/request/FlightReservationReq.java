package com.proj.flightservice.request;

import com.proj.flightservice.enums.SeatClass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public record FlightReservationReq(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Flight ID is required")
        Long flightId,

        @NotBlank(message = "Departure is required")
        String departure,

        @NotBlank(message = "Arrival is required")
        String arrival,

        @NotNull(message = "Departure time is required")
        LocalDateTime departureTime,

        @NotNull(message = "Arrival time is required")
        LocalDateTime arrivalTime,

        @NotEmpty(message = "At least one passenger is required")
        List<@Valid PassengerRequest> passengers
) {
    public record PassengerRequest(
            @NotBlank(message = "First name is required")
            String firstName,

            @NotBlank(message = "Last name is required")
            String lastName,

            @NotBlank(message = "Set class is required")
            SeatClass seat
    ) {}
}
