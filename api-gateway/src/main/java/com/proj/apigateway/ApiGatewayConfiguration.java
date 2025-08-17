package com.proj.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/api/v1/auth/**")
                        .uri("lb://auth-service"))
                .route(p -> p.path("/api/v1/accounts/**")
                        .uri("lb://account-service"))
                .route(p -> p.path("/api/v1/flights/**")
                        .uri("lb://flight-service"))
                .route(p -> p.path("/api/v1/hotels/**")
                        .uri("lb://hotel-service"))
                .route(p -> p.path("/api/v1/car-rentals/**")
                        .uri("lb://car-rental-service"))
                .route(p -> p.path("/api/v1/bookings/**")
                        .uri("lb://booking-service"))
                .route(p -> p.path("/api/v1/payments/**")
                        .uri("lb://payment-service"))
                .route(p -> p.path("/api/v1/notifications/**")
                        .uri("lb://notification-service"))
                .build();
    }
}
