# Travel Booking System - SAGA Choreography Pattern

This document describes the implementation of the SAGA (Saga Orchestration) pattern using choreography for the travel booking system.

## Overview

The travel booking system implements a SAGA choreography pattern to handle distributed transactions across multiple microservices:
- Booking Service (Orchestrator)
- Flight Service
- Hotel Service  
- Car Rental Service
- Payment Service

## SAGA Flow

### Happy Path (Successful Booking)
1. **Booking Created** → Booking Service creates initial booking record
2. **Flight Reserved** → Flight Service reserves seats
3. **Hotel Reserved** → Hotel Service reserves rooms (optional)
4. **Car Reserved** → Car Service reserves vehicles (optional)
5. **Payment Processed** → Payment Service processes payment
6. **Booking Confirmed** → Booking Service marks booking as confirmed

**Flexible Flow**: The SAGA automatically adapts based on what's included in the booking request:
- **Flight Only**: Flight → Payment → Confirmed
- **Flight + Hotel**: Flight → Hotel → Payment → Confirmed
- **Flight + Car**: Flight → Car → Payment → Confirmed
- **Flight + Hotel + Car**: Flight → Hotel → Car → Payment → Confirmed

### Compensation Path (Failure Handling)
If any step fails, the system automatically triggers compensation actions:

- **Flight Reservation Failed** → Booking marked as failed
- **Hotel Reservation Failed** → Cancel flight reservation, mark booking as failed
- **Car Reservation Failed** → Cancel flight & hotel reservations, mark booking as failed
- **Payment Failed** → Cancel all reservations (flight, hotel, car), mark booking as failed

## Event Structure

### Booking Request DTO
```json
{
  "userId": "12345",
  "flight": {
    "flightId": "FL123",
    "departure": "DEL",
    "arrival": "NYC",
    "date": "2025-09-01",
    "passengerDetails": [
      {
        "firstName": "Atul",
        "lastName": "Kumar",
        "seat": "12A"
      }
    ]
  },
  "hotel": {
    "hotelId": "H567",
    "checkInDate": "2025-09-01",
    "checkOutDate": "2025-09-05",
    "guests": 2,
    "roomType": "Deluxe"
  },
  "car": {
    "carId": "C890",
    "pickupDate": "2025-09-01",
    "dropoffDate": "2025-09-05",
    "pickupLocation": "JFK Airport"
  },
  "payment": {
    "paymentMethod": "CREDIT_CARD",
    "amount": 1200.00,
    "currency": "USD"
  }
}
```

**Note**: `hotel` and `car` fields are optional. The SAGA will automatically skip these steps if not provided.

### SAGA Events

#### Success Events
- `BookingCreatedEvent` - Initial booking creation
- `FlightReservedEvent` - Flight reservation successful
- `HotelReservedEvent` - Hotel reservation successful
- `CarReservedEvent` - Car reservation successful
- `PaymentProcessedEvent` - Payment processing successful
- `BookingCompletedEvent` - Complete booking successful

#### Failure Events
- `FlightReservationFailedEvent` - Flight reservation failed
- `HotelReservationFailedEvent` - Hotel reservation failed
- `CarReservationFailedEvent` - Car reservation failed
- `PaymentFailedEvent` - Payment processing failed
- `BookingFailedEvent` - Complete booking failed

#### Compensation Events
- `FlightCancelledEvent` - Flight reservation cancelled
- `HotelCancelledEvent` - Hotel reservation cancelled
- `CarCancelledEvent` - Car reservation cancelled
- `PaymentRefundedEvent` - Payment refunded

## API Endpoints

### Booking Service
- `POST /api/v1/bookings` - Create booking (triggers SAGA)
- `GET /api/v1/bookings/{id}` - Get booking status
- `GET /api/v1/bookings/me` - Get user's bookings

### Flight Service
- `GET /api/v1/flights` - List available flights
- `GET /api/v1/flights/{id}` - Get flight details
- `POST /api/v1/flights/reserve` - Reserve flight (SAGA step)
- `POST /api/v1/flights/cancel` - Cancel flight (compensation)

### Hotel Service
- `GET /api/v1/hotels` - List available hotels
- `GET /api/v1/hotels/{id}` - Get hotel details
- `POST /api/v1/hotels/reserve` - Reserve hotel (SAGA step)
- `POST /api/v1/hotels/cancel` - Cancel hotel (compensation)

### Car Rental Service
- `GET /api/v1/cars` - List available cars
- `GET /api/v1/cars/{id}` - Get car details
- `POST /api/v1/cars/reserve` - Reserve car (SAGA step)
- `POST /api/v1/cars/cancel` - Cancel car (compensation)

### Payment Service
- `POST /api/v1/payments` - Process payment (SAGA step)
- `POST /api/v1/payments/refund` - Refund payment (compensation)
- `GET /api/v1/payments/{bookingId}` - Get payment status

## Technology Stack

- **Spring Boot 3.x** - Application framework
- **Spring WebFlux** - Reactive web framework
- **Spring Cloud Stream** - Event streaming
- **Apache Kafka** - Message broker
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Database
- **Eureka** - Service discovery
- **Lombok** - Code generation
- **Validation** - Request validation

## Configuration

### Spring Cloud Stream Configuration
```properties
# Kafka Configuration
spring.cloud.stream.kafka.binder.brokers=localhost:9092
spring.cloud.stream.kafka.binder.required-acks=1
spring.cloud.stream.kafka.binder.replication-factor=1

# Event Topics
spring.cloud.stream.bindings.booking-created.destination=booking-created
spring.cloud.stream.bindings.flight-reserved.destination=flight-reserved
spring.cloud.stream.bindings.hotel-reserved.destination=hotel-reserved
spring.cloud.stream.bindings.car-reserved.destination=car-reserved
spring.cloud.stream.bindings.payment-processed.destination=payment-processed
```

## Running the System

1. **Start Infrastructure**
   ```bash
   # Start Kafka
   docker-compose up -d kafka
   
   # Start PostgreSQL
   docker-compose up -d postgres
   
   # Start Eureka
   docker-compose up -d eureka
   ```

2. **Start Services**
   ```bash
   # Start all services
   mvn spring-boot:run -pl booking-service
   mvn spring-boot:run -pl flight-service
   mvn spring-boot:run -pl hotel-service
   mvn spring-boot:run -pl car-rental-service
   mvn spring-boot:run -pl payment-service
   ```

3. **Create Booking**
   ```bash
   curl -X POST http://localhost:8082/api/v1/bookings \
     -H "Content-Type: application/json" \
     -H "X-USER-ID: 12345" \
     -d @booking-request.json
   ```

## Monitoring

### Booking Status Tracking
- `PENDING` - Initial state
- `FLIGHT_RESERVED` - Flight reserved
- `HOTEL_RESERVED` - Hotel reserved
- `CAR_RESERVED` - Car reserved
- `PAYMENT_PROCESSED` - Payment processed
- `CONFIRMED` - Booking completed
- `FAILED` - Booking failed
- `CANCELLED` - Booking cancelled

### Event Monitoring
All SAGA events are logged with correlation IDs for tracking:
```log
INFO  - Published booking created event for booking: abc-123-def
INFO  - Flight reserved successfully for booking: abc-123-def
INFO  - Hotel reserved successfully for booking: abc-123-def
INFO  - Car reserved successfully for booking: abc-123-def
INFO  - Payment processed successfully for booking: abc-123-def
INFO  - Booking completed successfully: abc-123-def
```

## Error Handling

### Compensation Logic
The system implements automatic compensation when any step fails:

1. **Flight Reservation Fails** → Booking marked as failed
2. **Hotel Reservation Fails** → Flight cancelled, booking marked as failed
3. **Car Reservation Fails** → Flight & hotel cancelled, booking marked as failed
4. **Payment Fails** → All reservations cancelled, booking marked as failed

### Idempotency
All operations are designed to be idempotent to handle duplicate events.

### Dead Letter Queue
Failed events are sent to dead letter queues for manual intervention.

## Testing

### Unit Tests
```bash
mvn test -pl booking-service
mvn test -pl flight-service
```

### Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### SAGA Flow Tests
```bash
# Test successful booking flow
./test-saga-success.sh

# Test failure scenarios
./test-saga-failure.sh
```

## Best Practices

1. **Event Sourcing** - All state changes are captured as events
2. **CQRS** - Separate read and write models
3. **Idempotency** - Operations can be safely retried
4. **Compensation** - Automatic rollback on failures
5. **Monitoring** - Comprehensive logging and metrics
6. **Validation** - Input validation at all layers
7. **Security** - JWT authentication and authorization

## Future Enhancements

1. **Saga State Machine** - Visual representation of SAGA flows
2. **Compensation Monitoring** - Dashboard for compensation events
3. **Event Replay** - Ability to replay events for debugging
4. **Performance Optimization** - Parallel processing where possible
5. **Circuit Breaker** - Resilience patterns for external services
