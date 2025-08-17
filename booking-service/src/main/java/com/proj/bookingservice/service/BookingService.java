package com.proj.bookingservice.service;

import com.proj.bookingservice.dto.BookingResponseDto;
import com.proj.common.dto.BookingRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingService {
    Mono<BookingResponseDto> createBooking(String userId, BookingRequestDto bookingRequest);
    Mono<BookingResponseDto> getBookingById(String id);
    Flux<BookingResponseDto> getBookingsByUserId(String userId);
}
