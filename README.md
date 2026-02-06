# Portfolio Management System

A full-stack portfolio management application built with Spring Boot and React, designed to help users track and analyze their investment portfolios.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Workflow](#workflow)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)

## Overview

Portfolio Management System is a comprehensive web application that enables users to create, manage, and analyze their investment portfolios. The application provides real-time portfolio tracking, performance analytics, and detailed reporting capabilities.

## Features

- User account management
- Multiple portfolio creation and management
- Holdings tracking with real-time valuation
- Portfolio performance analytics
- Historical snapshots for trend analysis
- Target allocation management
- Interactive dashboard with charts and metrics
- Responsive design for mobile and desktop

## Architecture

The application follows a layered architecture pattern with clear separation of concerns:

### System Architecture

[Add architecture diagram here]

**Layer Breakdown:**

- **Client Layer**: React 18 with Vite for fast development and optimized builds
- **API Layer**: RESTful controllers handling HTTP requests and responses
- **DTO Layer**: Data Transfer Objects for API contract definition
- **Business Layer**: Service components containing business logic and calculations
- **Data Layer**: Spring Data JPA repositories for data access
- **Entity Layer**: JPA entities mapping to database tables
- **Database Layer**: MySQL for persistent data storage

## Workflow

The application follows a standard request-response workflow:

[Add workflow diagram here]

**Workflow Steps:**

1. User performs action in the UI
2. HTTP request sent to backend API
3. Request validation at controller level
4. Business logic processing in service layer
5. Database operations via repositories
6. Response preparation with DTOs
7. UI update with formatted data

## Technology Stack

### Backend

- Java 17
- Spring Boot 3.4.1
- Spring Data JPA
- Hibernate
- MySQL 8.0
- Maven
- Lombok
- Flyway (Database Migrations)

### Frontend

- React 18
- Vite
- JavaScript/ES6+
- CSS3
- Axios (HTTP Client)

### Development Tools

- Git
- IntelliJ IDEA / VS Code
- Postman (API Testing)

## Prerequisites

Before running the application, ensure you have the following installed:

- Java Development Kit (JDK) 17 or higher
- Node.js 16.x or higher
- npm 8.x or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher
- Git

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/portfolio-management-system.git
cd portfolio-management-system
```

### 2. Backend Setup
```bash
cd portfolioBackend
mvn clean install
```

### 3. Frontend Setup
```bash
cd portfoliofrontend
npm install
```

## Configuration

### Database Configuration

1. Create a MySQL database:
```sql
CREATE DATABASE portfolio_db;
```

2. Update `portfolioBackend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/portfolio_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

### Frontend Configuration

Update the API base URL in `portfoliofrontend/src/services/api.js`:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## Running the Application

### Start Backend Server
```bash
cd portfolioBackend
mvn spring-boot:run
```

The backend server will start at `http://localhost:8080`

### Start Frontend Development Server
```bash
cd portfoliofrontend
npm run dev
```

The frontend application will start at `http://localhost:5173`

### Access the Application

Open your browser and navigate to `http://localhost:5173`

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Key Endpoints

#### User Management

- `GET /users` - Get all users
- `GET /users/{id}` - Get user by ID
- `POST /users` - Create new user
- `PUT /users/{id}` - Update user
- `DELETE /users/{id}` - Delete user

#### Portfolio Management

- `GET /portfolios` - Get all portfolios
- `GET /portfolios/{id}` - Get portfolio by ID
- `POST /portfolios` - Create new portfolio
- `PUT /portfolios/{id}` - Update portfolio
- `DELETE /portfolios/{id}` - Delete portfolio
- `GET /portfolios/{id}/holdings` - Get portfolio holdings

#### Analytics

- `GET /analytics/portfolio/{id}` - Get portfolio analytics
- `GET /analytics/portfolio/{id}/performance` - Get performance metrics
- `GET /analytics/portfolio/{id}/allocation` - Get allocation breakdown


## Project Structure
```
portfolio-management-system/
├── portfolioBackend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/portfoliobackend/
│   │   │   │   ├── controller/
│   │   │   │   ├── service/
│   │   │   │   ├── repository/
│   │   │   │   ├── entity/
│   │   │   │   ├── dto/
│   │   │   │   └── PortfolioBackendApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── db/migration/
│   │   └── test/
│   └── pom.xml
│
└── portfoliofrontend/
    ├── src/
    │   ├── components/
    │   ├── pages/
    │   ├── services/
    │   ├── hooks/
    │   ├── styles/
    │   ├── App.jsx
    │   └── main.jsx
    ├── package.json
    └── vite.config.js
```

