package com.proj.common.request;

import com.proj.common.dto.BookingRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightReservationRequest {
    private String bookingId;
    private String userId;
    private BookingRequestDto.FlightRequest flightRequest;
    private BookingRequestDto fullBookingRequest;
}
