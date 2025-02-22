# Proxy Fox

Proxy Fox is a Spring Boot application that acts as an intelligent proxy for AI language model APIs. It provides a unified interface to multiple AI providers while offering smart routing, failover capabilities, and organization-specific configurations.

### Key Benefits
- **Provider Redundancy**: Automatically fails over to alternative providers if the primary provider is unavailable
- **Smart Routing**: Routes requests to specific providers based on organization and project requirements
- **Unified Interface**: Provides a consistent API regardless of the underlying AI provider (OpenAI standard)


## Technologies

- Java 21
- Spring Boot 3.4.1
- MongoDB
- SpringDoc OpenAPI (Swagger UI)
- Lombok

## Prerequisites

- JDK 21
- Maven
- MongoDB instance

## Building the Project

To build the project, run:

```bash
mvn clean install
```

## Running the Application

You can run the application using Maven:

```bash
mvn spring-boot:run
```

Or run the JAR file directly after building:

```bash
java -jar target/proxy-fox-1.0-SNAPSHOT.jar
```

## API Documentation

Once the application is running, you can access the Swagger UI documentation at:

```
http://localhost:8080/swagger-ui.html
```

## Project Structure

```
proxy-fox/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── nl/jimkaplan/foxy/
│   │   │       ├── controller/
│   │   │       │   └── ChatController.java
│   │   │       ├── model/
│   │   │       │   └── ChatRequest.java
│   │   │       └── ProxyApiApplication.java
│   │   └── resources/
│   └── test/
│       └── java/
│           └── nl/jimkaplan/foxy/controller/
│               └── ChatControllerTest.java
├── logs/
│   ├── application.log
│   └── dev.log
└── pom.xml
```

## Features

### AI Provider Management
- Dynamic provider selection and failover support
- Priority-based provider routing
- Organization and project-specific provider mapping
- Provider configuration management via REST API
- Support for multiple AI model providers

### Chat Completion API
- Standardized chat completion endpoint (`/v1/chat/completions`)
- Automatic model selection with provider defaults
- Support for streaming responses
- Configurable chat parameters (temperature, top_p, max_tokens, etc.)
- Organization and project-specific routing via headers

### Integration & Infrastructure
- MongoDB integration for provider configuration storage
- Comprehensive error handling and failover logic
- Detailed logging with marker-based categorization
- OpenAPI/Swagger documentation
- RESTful API design with proper error responses


## Configuration

The application uses Spring Boot's standard configuration approach. You can customize the application by modifying:

- `application.properties` for Spring Boot configuration
- MongoDB connection settings
- Logging levels and destinations

## Logging

The application maintains two log files:
- `logs/application.log`: Main application logs
- `logs/dev.log`: Development-specific logs

## Development

To start developing:

1. Clone the repository
2. Ensure you have the prerequisites installed
3. Configure your MongoDB connection
4. Import the project into your preferred IDE
5. Run the application locally