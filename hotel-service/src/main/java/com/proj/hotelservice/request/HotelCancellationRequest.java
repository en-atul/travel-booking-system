package com.proj.hotelservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelCancellationRequest {
    private String bookingId;
    private String userId;
    private String reservationId;
    private String reason;
}
