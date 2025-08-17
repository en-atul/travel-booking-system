package com.proj.bookingservice.controller;

import com.proj.bookingservice.dto.BookingResponseDto;
import com.proj.bookingservice.service.BookingService;
import com.proj.common.dto.BookingRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Mono<BookingResponseDto>> createBooking(
            @RequestHeader("X-USER-ID") String userId,
            @Valid @RequestBody BookingRequestDto bookingRequest) {
        
        return ResponseEntity.ok(bookingService.createBooking(userId, bookingRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mono<BookingResponseDto>> getBooking(@PathVariable String id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @GetMapping("/me")
    public ResponseEntity<Flux<BookingResponseDto>> getMyBookings(
            @RequestHeader("X-USER-ID") String userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }
}
