# Hospital Website

A web application for managing hospital operations including patient registration, doctor appointments, and medical records.

## Tech Stack
- **Backend:** Spring Boot, Spring Security, Spring Data JPA
- **Frontend:** React
- **Database:** MySQL
- **Authentication:** JWT + Google OAuth2
- **Containerization:** Docker

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 17
- Node.js

### Setup
1. Clone the repository
2. Create a `.env` file in the project root using `.env.example` as a template
3. Run the application:
```bash
docker compose up --build
```

## Environment Variables
Create a `.env` file based on `.env.example`:
```env
DB_URL=jdbc:mysql://db:3306/hospital_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
GOOGLE_CLIENT_ID=your_client_id
GOOGLE_CLIENT_SECRET=your_client_secret
JWT_SECRET=your_jwt_secret_min_32_chars
JWT_EXPIRATION=3600000
```

## Authentication

### JWT Login

POST /api/auth/login

### Google OAuth2 Login
GET /oauth2/authorization/google
New users are automatically registered as patients.

## Running Tests
```bash
cd backend
mvn test
```