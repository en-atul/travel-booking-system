package com.proj.carrentalservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarCancellationRequest {
    private String bookingId;
    private String userId;
    private String reservationId;
    private String reason;
}
