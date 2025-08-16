
🍽️ EasyServe Restaurant Management System

EasyServe RMS is a backend system for managing restaurant operations including orders, menu items, customer registrations, and reservations. This Java Spring Boot application is built with RESTful APIs and supports modern service-oriented architecture.

🚀 Features

👤 Customer registration and authentication

🛒 Order creation, tracking, and updates

🍔 Menu item management

🍽️ Reservation handling

📊 Kitchen statistics and analytics

📧 Notification system (mocked for MVP)

🔒 Basic security integration

✅ DTO-layered architecture

🛠️ Tech Stack

Java 17

Spring Boot

Maven

Lombok

JUnit 5 (for tests)

In-memory Storage (Map, AtomicLong)

🏁 Getting Started
📦 Prerequisites

Java 17+

Maven 3.6+

Git (optional)

🔧 Setup

Clone the repository

git clone https://github.com/melakuneet/EasyServe-RMS.git
cd EasyServe-RMS


Build the project

mvn clean package -U


Run the application

java -jar target/easyserve-restaurant-1.0.0.jar

🔓 Public API Endpoint

A publicly accessible endpoint has been added for health checks or testing:

GET /api/public
Response: "Hello from Public API!"

This endpoint is open and does not require authentication. Useful for verifying if the service is up and running.

🧪 Running Tests

To run the test suite (JUnit 5):

mvn test


Tests cover core business logic, DTO validation, and basic service-layer functionality. Additional tests can be added under src/test/java.

📂 Project Structure
src/main/java/com/easyserve/
├── config/               # Security & configuration classes
├── controller/           # REST API endpoints
├── dto/                  # Data Transfer Objects
├── model/                # Core business models
├── repository/           # In-memory mock repositories
├── security/             # Spring Security setup
├── service/              # Core business logic
└── EasyServeApplication.java  # Main Spring Boot class

🔍 API Endpoints Overview
Endpoint	Method	Description
/api/register	POST	Register a new user
/api/orders	POST	Create new order
/api/orders/{id}/status	PUT	Update order status
/api/kitchen/stats	GET	View kitchen analytics
/api/menu	GET	View menu items
/api/reservations	POST	Create reservation
🧭 Swagger/OpenAPI

Swagger/OpenAPI documentation can be integrated using Springdoc.

👨‍💻 Author

Etefworkie Melaku
GitHub: @melakunet