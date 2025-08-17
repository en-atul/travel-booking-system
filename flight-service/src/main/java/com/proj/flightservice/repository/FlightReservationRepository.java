package com.proj.flightservice.repository;

import com.proj.flightservice.model.FlightReservation;
import com.proj.flightservice.enums.FlightReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightReservationRepository extends JpaRepository<FlightReservation, String> {
    Optional<FlightReservation> findByBookingId(String bookingId);
    List<FlightReservation> findByUserId(String userId);
    List<FlightReservation> findByStatus(FlightReservationStatus status);
}
