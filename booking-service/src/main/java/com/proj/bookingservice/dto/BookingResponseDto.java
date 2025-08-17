package com.proj.bookingservice.dto;

import com.proj.bookingservice.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private String bookingId;
    private String userId;
    private BookingStatus status;
    private FlightBookingInfo flight;
    private HotelBookingInfo hotel;
    private CarBookingInfo car;
    private PaymentInfo payment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String errorMessage;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlightBookingInfo {
        private String flightId;
        private String reservationId;
        private String status;
        private String departure;
        private String arrival;
        private String date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HotelBookingInfo {
        private String hotelId;
        private String reservationId;
        private String status;
        private String checkInDate;
        private String checkOutDate;
        private String roomType;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarBookingInfo {
        private String carId;
        private String reservationId;
        private String status;
        private String pickupDate;
        private String dropoffDate;
        private String pickupLocation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private String transactionId;
        private String status;
        private BigDecimal amount;
        private String currency;
        private String paymentMethod;
    }
}
