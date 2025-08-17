package com.proj.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDto {
    
    @Valid
    @NotNull(message = "Flight details are required")
    private FlightRequest flight;
    
    @Valid
    private HotelRequest hotel;
    
    @Valid
    private CarRequest car;
    
    @Valid
    @NotNull(message = "Payment details are required")
    private PaymentRequest payment;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightRequest {
        @NotBlank(message = "Flight ID is required")
        private String flightId;
        
        @NotBlank(message = "Departure airport is required")
        private String departure;
        
        @NotBlank(message = "Arrival airport is required")
        private String arrival;
        
        @NotNull(message = "Flight date is required")
        private LocalDate date;
        
        @NotNull(message = "Passenger details are required")
        private List<PassengerDetail> passengerDetails;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PassengerDetail {
        @NotBlank(message = "First name is required")
        private String firstName;
        
        @NotBlank(message = "Last name is required")
        private String lastName;
        
        @NotBlank(message = "Seat is required")
        private String seat;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotelRequest {
        @NotBlank(message = "Hotel ID is required")
        private String hotelId;
        
        @NotNull(message = "Check-in date is required")
        private LocalDate checkInDate;
        
        @NotNull(message = "Check-out date is required")
        private LocalDate checkOutDate;
        
        @NotNull(message = "Number of guests is required")
        @Positive(message = "Number of guests must be positive")
        private Integer guests;
        
        @NotBlank(message = "Room type is required")
        private String roomType;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarRequest {
        @NotBlank(message = "Car ID is required")
        private String carId;
        
        @NotNull(message = "Pickup date is required")
        private LocalDate pickupDate;
        
        @NotNull(message = "Drop-off date is required")
        private LocalDate dropoffDate;
        
        @NotBlank(message = "Pickup location is required")
        private String pickupLocation;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentRequest {
        @NotBlank(message = "Payment method is required")
        private String paymentMethod;
        
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private BigDecimal amount;
        
        @NotBlank(message = "Currency is required")
        private String currency;
    }
}
