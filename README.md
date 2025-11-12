# AutoNexo Backend

> **Digital platform connecting vehicle owners with trusted mechanical workshops**

AutoNexo is a comprehensive backend system built with Spring Boot that powers mobile applications (Android & Flutter) for connecting car owners with mechanical workshops. The platform features complete digital maintenance history, geolocation-based workshop matching, bidirectional reviews, and subscription management.

## 🚀 Features

- **7 Bounded Contexts** following Domain-Driven Design principles
- **JWT Authentication** with role-based access control
- **Geolocation Matching** (1-50km configurable radius)
- **Digital Maintenance History** with ownership transfers
- **Bidirectional Trust & Reputation System**
- **Email Notifications** with HTML templates
- **Media Management** via Cloudinary
- **Subscription Payments** (simulated for demo)
- **Public API** for workshop search and catalogs
- **Swagger/OpenAPI** documentation

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Detailed Documentation](#-detailed-documentation)
- [Contributing](#-contributing)

## 🛠️ Tech Stack

- **Framework**: Spring Boot
- **Language**: Java 25
- **Database**: MySQL
- **ORM**: Spring Data JPA + Hibernate
- **Security**: Spring Security + JWT
- **Email**: JavaMailSender (SMTP)
- **Media**: Cloudinary
- **API Docs**: Swagger/OpenAPI 3
- **Build**: Maven

## 🏗️ Architecture

AutoNexo follows **Domain-Driven Design (DDD)** with 7 bounded contexts:

1. **IAM Context** - Identity & Access Management
2. **Workshop Context** - Workshop profiles and configuration
3. **Vehicle & Maintenance Context** - Vehicle registration and history
4. **Matching & Booking Context** - Service requests and scheduling
5. **Trust & Reputation Context** - Bidirectional review system
6. **Notifications Context** - Email notifications
7. **Payment Context** - Subscription management

Each context communicates through **Anti-Corruption Layers (ACL)** to maintain loose coupling.

## 📚 API Documentation

### Public Endpoints (no authentication)

- `POST /api/v1/users/signup` - Register user
- `POST /api/v1/users/signin` - Login
- `GET /api/v1/workshops/search` - Search workshops
- `GET /api/v1/workshops/catalog/**` - Service catalogs

### Authenticated Endpoints

All other endpoints require JWT authentication:

```bash
Authorization: Bearer <your_jwt_token>
```

### Example: Register and Login

```bash
# Register
curl -X POST http://localhost:8080/api/v1/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe",
    "role": "CAR_OWNER"
  }'

# Login
curl -X POST http://localhost:8080/api/v1/users/signin \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass123!"
  }'
```

### Roles

- `CAR_OWNER` - Vehicle owners
- `WORKSHOP_MANAGER` - Workshop administrators
- `WORKSHOP_WORKER` - Workshop staff
- `ADMIN` - System administrators

### Public Catalog Endpoints

The application provides standardized catalogs for frontend applications:

```bash
# Vehicle Brands & Models
GET /api/v1/catalog/brands?popularOnly=true
GET /api/v1/catalog/brands/{brandId}/models

# Service Catalog (~60 services)
GET /api/v1/catalog/services
GET /api/v1/catalog/services?category=MAINTENANCE

# Capability Tags (~50 tags)
GET /api/v1/catalog/capability-tags
GET /api/v1/catalog/capability-tags?category=BRAND
```

See [`prompt/catalog/implementation.md`](prompt/catalog/implementation.md) for complete catalog documentation.

Full API documentation available at `/swagger-ui.html` when running.

## 📁 Project Structure

```
autonexo-backend/
├── src/main/java/com/atg/autonexo/backend/
│   ├── iam/                    # Identity & Access Management
│   ├── workshop/               # Workshop Management
│   ├── vehicle/                # Vehicle & Maintenance
│   ├── matching/               # Matching & Booking
│   ├── trust/                  # Trust & Reputation
│   ├── notifications/          # Notifications
│   ├── payment/                # Payment Management
│   └── shared/                 # Shared domain models
├── src/main/resources/
│   ├── application-dev.properties
│   ├── application-prod.properties
│   └── templates/emails/       # Email HTML templates
├── prompt/                     # Detailed documentation
│   ├── CORE.md                 # System overview
│   ├── OVERVIEW.md             # Architecture details
│   ├── API_ENDPOINTS.md        # Complete API listing
│   ├── DEPLOYMENT.md           # Deployment guide
│   ├── iam/
│   ├── workshop/
│   ├── vehicle/
│   ├── matching/
│   ├── trust/
│   ├── notifications/
│   └── payment/
└── pom.xml
```

## 📖 Detailed Documentation

For comprehensive documentation, see the `prompt/` directory:

- **[CORE.md](prompt/CORE.md)** - System overview and all 7 bounded contexts
- **[OVERVIEW.md](prompt/OVERVIEW.md)** - Architecture, patterns, and design decisions
- **[API_ENDPOINTS.md](prompt/API_ENDPOINTS.md)** - Complete API endpoint listing
- **[DEPLOYMENT.md](prompt/DEPLOYMENT.md)** - Deployment and configuration guide
- **[Catalog System](prompt/catalog/implementation.md)**  - Vehicle brands, models, services & tags

### Context-Specific Documentation

- [IAM Context](prompt/iam/improvements.md)
- [Workshop Context](prompt/workshop/improvements.md)
- [Vehicle & Maintenance](prompt/vehicle/implementation.md)
- [Matching & Booking](prompt/matching/implementation.md)
- [Trust & Reputation](prompt/trust/implementation.md)
- [Notifications](prompt/notifications/implementation.md)
- [Payment](prompt/payment/implementation.md)

## 🔒 Security

- **JWT Tokens**: Stateless authentication with configurable expiration
- **BCrypt**: Password hashing with salt
- **Role-Based Access**: Fine-grained permission control
- **Email Verification**: Mandatory email verification
- **Password Reset**: Secure token-based reset flow
- **CORS**: Configured for mobile app origins

## 🚢 Deployment

### Production Build

```bash
mvn clean package -Pprod -DskipTests
```

### Run Production

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker (Optional)

```bash
docker build -t autonexo-backend .
docker run -p 8080:8080 --env-file .env autonexo-backend
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Authors

- Development Team - ATG 2025-20

## 📞 Support

For issues and questions:
- Support Contact [https://www.instagram.com/rafavivancoo]
