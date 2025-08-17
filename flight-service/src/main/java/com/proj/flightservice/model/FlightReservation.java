package com.proj.flightservice.model;

import com.proj.flightservice.enums.FlightReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "flight_reservations")
public class FlightReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String bookingId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String flightId;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String arrival;

    @Column(nullable = false)
    private LocalDateTime flightDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightReservationStatus status;

    @Column(nullable = true)
    private String errorMessage;

    @ElementCollection
    @CollectionTable(name = "flight_reservation_passengers", joinColumns = @JoinColumn(name = "reservation_id"))
    @Column(name = "passenger_details")
    private List<String> passengerDetails;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
