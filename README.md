# Travel Booking System - Microservices Architecture

A comprehensive microservices-based travel booking system built with Spring Boot, Spring Cloud, and Maven.

## 🏗️ Architecture Overview

This project follows a microservices architecture with the following services:

### Core Services
- **API Gateway** (Port: 8765) - Central entry point with JWT authentication
- **Auth Service** (Port: 8200) - User authentication and authorization
- **Config Server** (Port: 8888) - Centralized configuration management
- **Naming Server** (Port: 8761) - Service discovery (Eureka)

### Business Services
- **Account Service** - Account management
- **Booking Service** - Booking management
- **Car Rental Service** - Car rental management
- **Flight Service** - Flight booking management
- **Hotel Service** - Hotel booking management
- **Payment Service** - Payment processing
- **Notification Service** - Asynchronous notifications via Kafka

### Shared Libraries
- **Common DTO** - Shared data transfer objects
- **Shared JWT Util** - JWT utilities for authentication

## 🚀 Quick Start

### Prerequisites
- Java 21
- Docker and Docker Compose
- Git

### 1. Clone the Repository
```bash
git clone <repository-url>
cd travel-booking-system
```

### 2. Start Infrastructure Services
```bash
# Start PostgreSQL, Kafka, Zookeeper, and monitoring tools
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

### 3. Build All Services
```bash
# Build all modules from root directory
./mvnw clean compile
```

### 4. Start Services in Order

#### Start Naming Server (Service Discovery)
```bash
./mvnw spring-boot:run -pl naming-server
```

#### Start Config Server
```bash
./mvnw spring-boot:run -pl config-server
```

#### Start API Gateway
```bash
./mvnw spring-boot:run -pl api-gateway
```

#### Start Auth Service
```bash
./mvnw spring-boot:run -pl auth-service
```

#### Start Other Business Services
```bash
# Start all business services
./mvnw spring-boot:run -pl account-service &
./mvnw spring-boot:run -pl booking-service &
./mvnw spring-boot:run -pl car-rental-service &
./mvnw spring-boot:run -pl flight-service &
./mvnw spring-boot:run -pl hotel-service &
./mvnw spring-boot:run -pl payment-service &
```

## 📁 Project Structure

```
travel-booking-system/
├── mvnw                        ← Maven wrapper (use this for all commands)
├── mvnw.cmd                    ← Maven wrapper (Windows)
├── pom.xml                     ← Parent POM
├── config-server/              ← Configuration server
├── naming-server/              ← Service discovery (Eureka)
├── api-gateway/                ← API Gateway
├── auth-service/               ← Authentication service
├── account-service/            ← Account management
├── booking-service/            ← Booking management
├── car-rental-service/         ← Car rental service
├── flight-service/             ← Flight booking service
├── hotel-service/              ← Hotel booking service
├── payment-service/            ← Payment processing
├── common-dto/                 ← Shared DTOs
├── shared-jwt-util/            ← JWT utilities
├── docker-compose.yml          ← Base Docker Compose
└── docker-compose.dev.yml      ← Development overrides
```

## 🔧 Development Commands

### Build Commands
```bash
# Build all modules
./mvnw clean compile

# Build specific module
./mvnw clean compile -pl auth-service

# Build multiple modules
./mvnw clean compile -pl auth-service,config-server

# Build all except one module
./mvnw clean compile -pl '!auth-service'
```

### Run Commands
```bash
# Run specific service
./mvnw spring-boot:run -pl auth-service

# Run with specific profile
./mvnw spring-boot:run -pl auth-service -Dspring-boot.run.profiles=dev

# Run multiple services (in background)
./mvnw spring-boot:run -pl auth-service &
./mvnw spring-boot:run -pl config-server &
```

### Test Commands
```bash
# Run tests for all modules
./mvnw test

# Run tests for specific module
./mvnw test -pl auth-service

# Run tests with coverage
./mvnw test jacoco:report
```

### Package Commands
```bash
# Package all modules
./mvnw clean package

# Package specific module
./mvnw clean package -pl auth-service

# Create Docker images
./mvnw clean package -DskipTests
```

## 🌐 Service URLs

Once all services are running:

- **Naming Server**: http://localhost:8761
- **Config Server**: http://localhost:8888
- **API Gateway**: http://localhost:8765
- **Auth Service**: http://localhost:8200
- **Kafka UI**: http://localhost:4002
- **Kafdrop**: http://localhost:9000

## 📊 Monitoring

- **Eureka Dashboard**: http://localhost:8761 - View all registered services
- **Kafka UI**: http://localhost:4002 - Monitor Kafka topics and messages
- **Kafdrop**: http://localhost:9000 - Alternative Kafka monitoring

## 🔐 Configuration

The system uses Spring Cloud Config Server for centralized configuration management. Configuration files are stored in the external repository: `travel-booking-system-config`

### Configuration Structure
```
travel-booking-system-config/
├── auth-service/
│   ├── auth-service.properties
│   ├── auth-service-dev.properties
│   └── auth-service-prod.properties
├── account-service/
│   ├── account-service.properties
│   └── account-service-dev.properties
└── ... (other services)
```

## 🐳 Docker Support

### Build Docker Images
```bash
# Build all images
./mvnw clean package -DskipTests

# Build specific service image
./mvnw clean package -pl auth-service -DskipTests
```

### Run with Docker Compose
```bash
# Start infrastructure only
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Start all services (when Docker images are available)
docker-compose up -d
```

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
./mvnw test

# Run specific service tests
./mvnw test -pl auth-service
```

### Integration Tests
```bash
# Run integration tests
./mvnw verify
```

## 📝 API Documentation

Each service provides its own API documentation:
- **Auth Service**: http://localhost:8200/swagger-ui.html
- **API Gateway**: http://localhost:8765/swagger-ui.html

## 🔧 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Find process using port
   lsof -i :8761
   
   # Kill process
   kill -9 <PID>
   ```

2. **Maven Build Issues**
   ```bash
   # Clean and rebuild
   ./mvnw clean compile
   
   # Update dependencies
   ./mvnw dependency:resolve
   ```

3. **Docker Issues**
   ```bash
   # Restart Docker services
   docker-compose down
   docker-compose up -d
   ```

### Logs
```bash
# View service logs
./mvnw spring-boot:run -pl auth-service | tee auth-service.log

# View Docker logs
docker-compose logs -f auth-service
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `./mvnw test`
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section above
- Review the service-specific documentation
