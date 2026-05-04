# Hospital Website

A web application for managing hospital operations including patient registration, doctor appointments, and medical records.
## 📋 Table of Contents
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Environment Variables](#environment-variables)
- [Setup Instructions](#setup-instructions)
- [Run Commands](#run-commands)
- [Authentication](#authentication)
- [API Documentation](#api-documentation)


## Tech Stack
- **Backend:** Java17 , Spring Boot, Spring Security, Spring Data JPA
- **Frontend:** React , TypeScript, Vite 
- **Database:** MySQL 8
- **Authentication:** JWT + Google OAuth2
- **Containerization:** Docker , Docker Compose
- **CI/CD:** GitHub Actions
- **APIDocs:** Swagger / OpenAPI 3

---

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- MySQL 8 (if running locally without Docker)
- Maven 3.8+

---

## Environment Variables
Copy `.env.example` to `.env` and fill in the values:
```bash
cp .env.example .env
```

```env
DB_URL=jdbc:mysql://db:3306/hospital_db
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
GOOGLE_CLIENT_ID=your_client_id  //From Google Cloud Console
GOOGLE_CLIENT_SECRET=your_client_secret  //From Google Cloud Console
JWT_SECRET=your_jwt_secret_min_32_chars
JWT_EXPIRATION=3600000  //1 hour
```
### Setup Instructions
### Option 1 — Docker (Recommended)

```bash
# 1. Clone the repository
git clone https://github.com/HMS-SWE/Hospital-website.git
cd Hospital-website

# 2. Create environment file
cp .env.example .env
# Edit .env with your values

# 3. Start all services
docker compose up --build
```

Services will be available at:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui/index.html

---

### Option 2 — Local Development

#### Backend
```bash
cd backend

# Set environment variables (or use .env file with spring-dotenv)
export DB_URL=jdbc:mysql://localhost:3306/hospital_db
export DB_USERNAME=root
export DB_PASSWORD=yourpassword
export APP_JWT_SECRET=your-secret-key-min-32-characters
export APP_JWT_EXPIRATION=3600000
export GOOGLE_CLIENT_ID=your-client-id
export GOOGLE_CLIENT_SECRET=your-client-secret

# Run the backend
mvn spring-boot:run
```

#### Frontend
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

---

##  Run Commands

### Docker
```bash
# Start all services
docker compose up

# Start with rebuild
docker compose up --build

# Stop all services
docker compose down

# View logs
docker logs hospital-backend
docker logs hospital-frontend
docker logs hospital-db
```

### Backend
```bash
cd backend

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Run tests with coverage report
mvn test jacoco:report

# Build JAR
mvn clean package
```

### Frontend
```bash
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev

# Build for production
npm run build

# Run linter
npm run lint
```

---

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
---

## API Documentation

Swagger UI is available at:
http://localhost:8080/swagger-ui/index.html

OpenAPI JSON spec:
http://localhost:8080/v3/api-docs

---
## Security Notes

- All passwords are hashed using **BCrypt**
- JWT tokens expire after **1 hour** by default
- Google OAuth2 is used for patient self-registration
- Branch protection rules enforce CI checks before merging
- Secrets are stored in `.env` — never in code or GitHub
