package com.proj.bookingservice.service;

import com.proj.bookingservice.dto.BookingResponseDto;
import com.proj.bookingservice.enums.BookingStatus;
import com.proj.bookingservice.model.Booking;
import com.proj.bookingservice.repository.BookingRepository;
import com.proj.common.dto.BookingRequestDto;
import com.proj.common.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final StreamBridge streamBridge;

    @Override
    public Mono<BookingResponseDto> createBooking(String userId, BookingRequestDto bookingRequest) {
        return Mono.fromCallable(() -> {
            // Create initial booking record
            Booking booking = new Booking();
            booking.setId(UUID.randomUUID().toString());
            booking.setUserId(userId);
            booking.setStatus(BookingStatus.PENDING);
            booking.setTotalAmount(bookingRequest.getPayment().getAmount());
            booking.setCurrency(bookingRequest.getPayment().getCurrency());
            
            // Save booking
            booking = bookingRepository.save(booking);
            
            // Publish booking created event to start SAGA
            BookingCreatedEvent event = new BookingCreatedEvent(
                booking.getId(), 
                userId, 
                bookingRequest
            );
            
            streamBridge.send("booking-created", event);
            log.info("Published booking created event for booking: {}", booking.getId());
            
            return mapToResponseDto(booking);
        });
    }

    @Override
    public Mono<BookingResponseDto> getBookingById(String id) {
        return Mono.fromCallable(() -> {
            Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
            return mapToResponseDto(booking);
        });
    }

    @Override
    public Flux<BookingResponseDto> getBookingsByUserId(String userId) {
        return Flux.fromIterable(() -> {
            List<Booking> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
            return bookings.stream().map(this::mapToResponseDto).iterator();
        });
    }

    private BookingResponseDto mapToResponseDto(Booking booking) {
        BookingResponseDto response = new BookingResponseDto();
        response.setBookingId(booking.getId());
        response.setUserId(booking.getUserId());
        response.setStatus(booking.getStatus());
        response.setCreatedAt(booking.getCreatedAt());
        response.setUpdatedAt(booking.getUpdatedAt());
        response.setErrorMessage(booking.getErrorMessage());

        // Set flight info
        if (booking.getFlightReservationId() != null) {
            BookingResponseDto.FlightBookingInfo flightInfo = new BookingResponseDto.FlightBookingInfo();
            flightInfo.setReservationId(booking.getFlightReservationId());
            flightInfo.setStatus("RESERVED");
            response.setFlight(flightInfo);
        }

        // Set hotel info
        if (booking.getHotelReservationId() != null) {
            BookingResponseDto.HotelBookingInfo hotelInfo = new BookingResponseDto.HotelBookingInfo();
            hotelInfo.setReservationId(booking.getHotelReservationId());
            hotelInfo.setStatus("RESERVED");
            response.setHotel(hotelInfo);
        }

        // Set car info
        if (booking.getCarReservationId() != null) {
            BookingResponseDto.CarBookingInfo carInfo = new BookingResponseDto.CarBookingInfo();
            carInfo.setReservationId(booking.getCarReservationId());
            carInfo.setStatus("RESERVED");
            response.setCar(carInfo);
        }

        // Set payment info
        if (booking.getPaymentTransactionId() != null) {
            BookingResponseDto.PaymentInfo paymentInfo = new BookingResponseDto.PaymentInfo();
            paymentInfo.setTransactionId(booking.getPaymentTransactionId());
            paymentInfo.setStatus("PROCESSED");
            paymentInfo.setAmount(booking.getTotalAmount());
            paymentInfo.setCurrency(booking.getCurrency());
            response.setPayment(paymentInfo);
        }

        return response;
    }
}
