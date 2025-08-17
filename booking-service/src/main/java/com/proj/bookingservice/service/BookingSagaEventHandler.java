package com.proj.bookingservice.service;

import com.proj.bookingservice.enums.BookingStatus;
import com.proj.bookingservice.model.Booking;
import com.proj.bookingservice.repository.BookingRepository;
import com.proj.common.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingSagaEventHandler {

    private final BookingRepository bookingRepository;
    private final StreamBridge streamBridge;

    public void handleFlightReserved(FlightReservedEvent event) {
        log.info("Handling flight reserved event for booking: {}", event.getBookingId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(event.getBookingId());
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setFlightReservationId(event.getReservationId());
            booking.setStatus(BookingStatus.FLIGHT_RESERVED);
            bookingRepository.save(booking);
            
            // In Choreography pattern, we don't orchestrate the next steps
            // Each service will make its own decision based on the event
            log.info("Flight reserved for booking: {}. Waiting for next service to take action.", event.getBookingId());
        }
    }

    public void handleFlightReservationFailed(FlightReservationFailedEvent event) {
        log.info("Handling flight reservation failed event for booking: {}", event.getBookingId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(event.getBookingId());
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(BookingStatus.FAILED);
            booking.setErrorMessage(event.getErrorMessage());
            booking.setFailureReason("Flight reservation failed");
            booking.setFailedStep("FLIGHT_RESERVATION");
            bookingRepository.save(booking);
        }
    }

    public void handleHotelReserved(HotelReservedEvent event) {
        log.info("Handling hotel reserved event for booking: {}", event.getBookingId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(event.getBookingId());
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setHotelReservationId(event.getReservationId());
            booking.setStatus(BookingStatus.HOTEL_RESERVED);
            bookingRepository.save(booking);
            
            // In Choreography pattern, we don't orchestrate the next steps
            // Each service will make its own decision based on the event
            log.info("Hotel reserved for booking: {}. Waiting for next service to take action.", event.getBookingId());
        }
    }

    public void handleHotelReservationFailed(HotelReservationFailedEvent event) {
        log.info("Handling hotel reservation failed event for booking: {}", event.getBookingId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(event.getBookingId());
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(BookingStatus.FAILED);
            booking.setErrorMessage(event.getErrorMessage());
            booking.setFailureReason("Hotel reservation failed");
            booking.setFailedStep("HOTEL_RESERVATION");
            bookingRepository.save(booking);
            
            // Trigger flight cancellation compensation
            if (booking.getFlightReservationId() != null) {
                streamBridge.send("flight-cancel", new FlightCancelledEvent(
                    event.getBookingId(), 
                    event.getUserId(), 
                    booking.getFlightReservationId(), 
                    "Hotel reservation failed"
                ));
            }
        }
    }

    public void handleCarReserved(CarReservedEvent event) {
        log.info("Handling car reserved event for booking: {}", event.getBookingId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(event.getBookingId());
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setCarReservationId(event.getReservationId());
            booking.setStatus(BookingStatus.CAR_RESERVED);
            bookingRepository.save(booking);
            
            // In Choreography pattern, we don't orchestrate the next steps
            // Each service will make its own decision based on the event
            log.info("Car reserved for booking: {}. Waiting for next service to take action.", event.getBookingId());
        }
    }

    public void handleCarReservationFailed(CarReservationFailedEvent event) {
        log.info("Handling car reservation failed event for booking: {}", event.getBookingId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(event.getBookingId());
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(BookingStatus.FAILED);
            booking.setErrorMessage(event.getErrorMessage());
            booking.setFailureReason("Car reservation failed");
            booking.setFailedStep("CAR_RESERVATION");
            bookingRepository.save(booking);
            
            // Trigger compensation for previous reservations
            if (booking.getFlightReservationId() != null) {
                streamBridge.send("flight-cancel", new FlightCancelledEvent(
                    event.getBookingId(), 
                    event.getUserId(), 
                    booking.getFlightReservationId(), 
                    "Car reservation failed"
                ));
            }
            
            if (booking.getHotelReservationId() != null) {
                streamBridge.send("hotel-cancel", new HotelCancelledEvent(
                    event.getBookingId(), 
                    event.getUserId(), 
                    booking.getHotelReservationId(), 
                    "Car reservation failed"
                ));
            }
        }
    }

    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("Handling payment processed event for booking: {}", event.getBookingId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(event.getBookingId());
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setPaymentTransactionId(event.getTransactionId());
            booking.setStatus(BookingStatus.PAYMENT_PROCESSED);
            bookingRepository.save(booking);
            
            // Complete the booking
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            
            log.info("Booking completed successfully: {}", event.getBookingId());
        }
    }

    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("Handling payment failed event for booking: {}", event.getBookingId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(event.getBookingId());
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(BookingStatus.FAILED);
            booking.setErrorMessage(event.getErrorMessage());
            booking.setFailureReason("Payment failed");
            booking.setFailedStep("PAYMENT");
            bookingRepository.save(booking);
            
            // Trigger compensation for all previous reservations
            if (booking.getFlightReservationId() != null) {
                streamBridge.send("flight-cancel", new FlightCancelledEvent(
                    event.getBookingId(), 
                    event.getUserId(), 
                    booking.getFlightReservationId(), 
                    "Payment failed"
                ));
            }
            
            if (booking.getHotelReservationId() != null) {
                streamBridge.send("hotel-cancel", new HotelCancelledEvent(
                    event.getBookingId(), 
                    event.getUserId(), 
                    booking.getHotelReservationId(), 
                    "Payment failed"
                ));
            }
            
            if (booking.getCarReservationId() != null) {
                streamBridge.send("car-cancel", new CarCancelledEvent(
                    event.getBookingId(), 
                    event.getUserId(), 
                    booking.getCarReservationId(), 
                    "Payment failed"
                ));
            }
        }
    }

    public void handleBookingCompleted(BookingCompletedEvent event) {
        log.info("Handling booking completed event for booking: {}", event.getBookingId());
        
        Optional<Booking> bookingOpt = bookingRepository.findById(event.getBookingId());
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setPaymentTransactionId(event.getPaymentTransactionId());
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
            
            log.info("Booking completed successfully: {}", event.getBookingId());
        }
    }
}
