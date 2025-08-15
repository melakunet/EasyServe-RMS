# 🍽️ EasyServe Restaurant Management System

**EasyServe RMS** is a backend system for managing restaurant operations including orders, menu items, customer registrations, and reservations. This Java Spring Boot application is built with RESTful APIs and supports modern service-oriented architecture.

---

## 🚀 Features

- 👤 Customer registration and authentication
- 🛒 Order creation, tracking, and updates
- 🍔 Menu item management
- 🍽️ Reservation handling
- 📊 Kitchen statistics and analytics
- 📧 Notification system (mocked for MVP)
- 🔒 Basic security integration
- ✅ DTO-layered architecture

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot**
- **Maven**
- **Lombok**
- **JUnit 5** (for tests)
- **In-memory Storage** (`Map`, `AtomicLong`)

---

## 🏁 Getting Started

### 📦 Prerequisites

- Java 17+
- Maven 3.6+
- Git (optional)

### 🔧 Setup

⚙️ Build and Run
mvn clean install
mvn spring-boot:run

```bash
git clone https://github.com/melakuneet/EasyServe-RMS.git
cd EasyServe-RMS

📂 Project Structure
src/main/java/com/easyserve/
├── config/               # Security & config classes
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

Swagger/OpenAPI documentation can be integrated using Springdoc.

👨‍💻 Author

Etefworkie Melaku
GitHub: melakunet