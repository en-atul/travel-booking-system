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
                .route(p -> p.path("/api/v1/customers/**")
                        .uri("lb://customer-service"))
                .route(p -> p.path("/api/v1/transactions/**")
                        .uri("lb://transaction-service"))
                .route(p -> p.path("/api/v1/ledger-entries/**")
                        .uri("lb://ledger-service"))
                .route(p -> p.path("/api/v1/notifications/**")
                        .uri("lb://notification-service"))
                .build();
    }
}
