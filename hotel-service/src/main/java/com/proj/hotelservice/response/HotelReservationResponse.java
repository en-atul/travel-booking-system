package com.proj.hotelservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelReservationResponse {
    private String bookingId;
    private String reservationId;
    private String status;
    private String message;
}
