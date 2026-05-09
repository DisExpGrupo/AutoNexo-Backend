# AutoNexo Backend - Testing Guide (WIP - NO ESTA CONFIRMADO)

> Comprehensive guide to test all API endpoints with correct flow and example values.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Architecture Overview](#architecture-overview)
3. [Authentication](#authentication)
4. [Testing Flow](#testing-flow)
5. [Endpoint Reference](#endpoint-reference)
6. [Common Issues](#common-issues)

---

## Prerequisites

### Services Running

```bash
# 1. Start MySQL
docker-compose up -d

# 2. Verify MySQL is healthy
docker-compose ps

# 3. Start the application
./mvnw spring-boot:run
```

### URLs

| Service | URL |
|---------|-----|
| API Base | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Health Check | `http://localhost:8080/actuator/health` |

---

## Architecture Overview

### 7 Bounded Contexts

| Context | Purpose |
|---------|---------|
| **IAM** | Identity & Access Management (users, roles, auth) |
| **Workshop** | Workshop profiles, locations, staff management |
| **Vehicle** | Vehicle registration and maintenance history |
| **Matching** | Service requests, offers, bookings |
| **Trust** | Bidirectional reviews and reputation |
| **Notifications** | Email notifications |
| **Payment** | Subscription management (simulated) |

### Roles

| Role | Description |
|------|-------------|
| `CAR_OWNER` | Vehicle owners searching for workshops |
| `WORKSHOP_MANAGER` | Workshop administrators |
| `WORKSHOP_WORKER` | Workshop staff |
| `ADMIN` | System administrators |

---

## Authentication

### Public Endpoints (No Auth Required)

```
POST /api/v1/users/signup   - Register new user
POST /api/v1/users/signin    - Login and get JWT token
GET  /api/v1/workshops/search - Search workshops (public)
GET  /api/v1/catalog/**      - Public catalogs
```

### Authenticated Requests

All other endpoints require:

```bash
Authorization: Bearer <your-jwt-token>
```

---

## Testing Flow

### Step 1: Register Users

Create at least two users - one CAR_OWNER and one WORKSHOP_MANAGER.

> **Note:** `phoneNumber` and `invitationCode` are optional fields. You can omit them or leave them with their default values

#### 1.1 Register as Car Owner

```bash
curl -X POST http://localhost:8080/api/v1/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "stringstri",
    "requestedRole": "CAR_OWNER",
    "invitationCode": "string"
  }'
```

**Response (201 Created):**
```json
"User registered successfully"
```

#### 1.2 Register as Workshop Manager

```bash
curl -X POST http://localhost:8080/api/v1/users/signup \
  -H "Content-Type: application/json" \
  -d '{
    "email": "boss@workshop.com",
    "password": "SecurePass123!",
    "firstName": "Workshop",
    "lastName": "Owner",
    "phoneNumber": "stringstri",
    "requestedRole": "WORKSHOP_MANAGER",
    "invitationCode": "string"
  }'
```

---

### Step 2: Login (Get JWT Token)

> **Note:** Login uses `username` (not email). The username is auto-generated or can be set. Based on the signup response, the system may auto-generate a username from the email or you may need to check the response.

#### 2.1 Login as Car Owner

```bash
curl -X POST http://localhost:8080/api/v1/users/signin \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 604800,
  "user": {
    "id": 1,
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "stringstri",
    "isVerified": false,
    "active": true,
    "roles": [
      "CAR_OWNER"
    ],
    "workshopId": null,
    "createdAt": "2026-05-08T17:27:32.903+00:00",
    "updatedAt": "2026-05-08T17:27:32.903+00:00"
  }
}
```

Save the token - you'll need it for authenticated requests.

#### 2.2 Login as Workshop Manager

```bash
curl -X POST http://localhost:8080/api/v1/users/signin \
  -H "Content-Type: application/json" \
  -d '{
    "email": "boss@workshop.com",
    "password": "SecurePass123!"
  }'
```

---

### Step 3: Create Workshop (Workshop Manager)

This step requires the WORKSHOP_MANAGER token.

```bash
curl -X POST http://localhost:8080/api/v1/workshops \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <WORKSHOP_MANAGER_TOKEN>" \
  -d '{
    "ownerUserId": 2,
    "name": "Premium Auto Service",
    "shortDescription": "Expert mechanical services for all vehicle brands",
    "legalName": "AutoNexo",
    "ruc": "12345678910"
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "ownerUserId": 2,
  "name": "Premium Auto Service",
  "shortDescription": "Expert mechanical services for all vehicle brands",
  "legalName": "AutoNexo",
  "ruc": "12345678910",
  "rucVerified": false,
  "trustScore": null,
  "active": true,
  "deletedAt": null,
  "logoUrl": null,
  "photoUrls": [],
  "capabilityTags": [],
  "createdAt": "2026-05-08T17:53:43.540+00:00",
  "updatedAt": "2026-05-08T17:53:43.540+00:00"
}
```

---

### Step 4: Add Workshop Location

Workshop locations are required for the matching service to work.

```bash
curl -X POST http://localhost:8080/api/v1/workshops/locations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <WORKSHOP_MANAGER_TOKEN>" \
  -d '{
    "street": "123 Auto Street",
    "city": "San Jose",
    "state": "California",
    "zip": "95101",
    "country": "USA",
    "latitude": 37.3382,
    "longitude": -121.8863
  }'
```

**Important:** The matching service uses latitude/longitude to find nearby workshops. Coordinates are required.

---

### Step 5: Add Service Templates

Service templates define what services the workshop offers.

```bash
curl -X POST http://localhost:8080/api/v1/workshops/service-templates \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <WORKSHOP_MANAGER_TOKEN>" \
  -d '{
    "name": "Oil Change",
    "description": "Full synthetic oil change with filter replacement",
    "estimatedDurationMinutes": 45,
    "basePrice": 59.99,
    "serviceCatalogCode": "OIL_CHANGE"
  }'
```

**Available Service Catalog Codes:**

| Code | Name |
|------|------|
| `OIL_CHANGE` | Oil Change |
| `BRAKE_PAD_REPLACEMENT` | Brake Pad Replacement |
| `TIRE_ROTATION` | Tire Rotation |
| `AIR_FILTER_REPLACEMENT` | Air Filter Replacement |
| `TRANSMISSION_SERVICE` | Transmission Service |
| `COOLANT_FLUSH` | Coolant Flush |
| `BATTERY_REPLACEMENT` | Battery Replacement |
| `SPARK_PLUG_REPLACEMENT` | Spark Plug Replacement |
| `TIMING_BELT_REPLACEMENT` | Timing Belt Replacement |
| `WHEEL_ALIGNMENT` | Wheel Alignment |

---

### Step 6: Add Capability Tags (Optional)

Tags help improve workshop visibility in search results.

```bash
curl -X POST http://localhost:8080/api/v1/workshops/tags \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <WORKSHOP_MANAGER_TOKEN>" \
  -d '{
    "tag": "BRAND_PREMIUM"
  }'
```

**Available Tag Categories:**

| Category | Example Tags |
|----------|--------------|
| `BRAND` | BRAND_GERMAN, BRAND_JAPANESE, BRAND_AMERICAN, BRAND_PREMIUM |
| `SPECIALTY` | SPECIALTY_ELECTRIC, SPECIALTY_HYBRID, SPECIALTY_DIESEL |
| `SERVICE` | SERVICE_24H, SERVICE_MOBILE, SERVICE_DIAGNOSIS |

---

### Step 7: Register Vehicle (Car Owner)

```bash
curl -X POST http://localhost:8080/api/v1/vehicles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <CAR_OWNER_TOKEN>" \
  -d '{
    "brandId": 1,
    "model": "Camry",
    "year": 2022,
    "licensePlate": "ABC-1234",
    "vin": "1HGBH41JXMN109186",
    "currentMileage": 15000
  }'
```

**Note:** `brandId` must reference a valid brand from the catalog. Check `GET /api/v1/catalog/brands` for available brands.

---

### Step 8: Search Workshops (Matching)

The car owner searches for workshops based on location and services.

```bash
curl -X GET "http://localhost:8080/api/matching/workshops?latitude=37.3382&longitude=-121.8863&radiusKm=25" \
  -H "Authorization: Bearer <CAR_OWNER_TOKEN>"
```

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `latitude` | double | Yes | User's latitude |
| `longitude` | double | Yes | User's longitude |
| `radiusKm` | int | Yes | Search radius (1-50 km) |
| `services` | string[] | No | Service codes to filter |

**Response:**
```json
{
  "workshops": [
    {
      "workshopId": 1,
      "name": "Premium Auto Service",
      "matchScore": 85.5,
      "distanceKm": 2.3,
      "rating": 4.8,
      "services": ["OIL_CHANGE", "BRAKE_PAD_REPLACEMENT"]
    }
  ]
}
```

---

### Step 9: Create Service Request

The car owner creates a service request for the workshop to respond to.

```bash
curl -X POST http://localhost:8080/api/service-requests \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <CAR_OWNER_TOKEN>" \
  -d '{
    "vehicleId": 1,
    "requestedServices": ["OIL_CHANGE", "BRAKE_PAD_REPLACEMENT"],
    "description": "Need regular maintenance and brake check",
    "userLocation": {
      "latitude": 37.3382,
      "longitude": -121.8863
    },
    "searchRadiusKm": 25
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "vehicleId": 1,
  "requestedServices": ["OIL_CHANGE", "BRAKE_PAD_REPLACEMENT"],
  "description": "Need regular maintenance and brake check",
  "status": "PENDING",
  "matches": [...],
  "createdAt": "2026-05-08T12:00:00Z"
}
```

---

### Step 10: Workshop Views Available Requests

```bash
curl -X GET http://localhost:8080/api/v1/workshops/my-workshop/available-requests \
  -H "Authorization: Bearer <WORKSHOP_MANAGER_TOKEN>"
```

---

### Step 11: Workshop Creates Offer

```bash
curl -X POST http://localhost:8080/api/offers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <WORKSHOP_MANAGER_TOKEN>" \
  -d '{
    "serviceRequestId": 1,
    "proposedPrice": 189.99,
    "proposedDate": "2026-05-15",
    "notes": "We have all parts in stock. Service takes about 2 hours."
  }'
```

**Response:**
```json
{
  "id": 1,
  "serviceRequestId": 1,
  "workshopId": 1,
  "proposedPrice": 189.99,
  "proposedDate": "2026-05-15",
  "status": "PENDING",
  "expiresAt": "2026-05-09T12:00:00Z"
}
```

---

### Step 12: Car Owner Accepts Offer

```bash
curl -X POST http://localhost:8080/api/offers/1/accept \
  -H "Authorization: Bearer <CAR_OWNER_TOKEN>"
```

**This creates a ServiceBooking with status `PENDING_SCHEDULE`.**

---

### Step 13: Confirm Schedule

```bash
curl -X POST http://localhost:8080/api/service-bookings/1/confirm-schedule \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <CAR_OWNER_TOKEN>" \
  -d '{
    "scheduledDate": "2026-05-15T10:00:00Z"
  }'
```

**Status changes to `SCHEDULED`.**

---

### Step 14: Complete Service (Workshop)

```bash
curl -X POST http://localhost:8080/api/service-bookings/1/complete \
  -H "Authorization: Bearer <WORKSHOP_MANAGER_TOKEN>" \
  -d '{
    "finalPrice": 189.99,
    "notes": "Brake pads still at 40%. Recommended replacement at next service."
  }'
```

**Status changes to `PENDING_PICKUP`.**

---

### Step 15: Confirm Pickup (Car Owner)

```bash
curl -X POST http://localhost:8080/api/service-bookings/1/confirm-pickup \
  -H "Authorization: Bearer <CAR_OWNER_TOKEN>"
```

**Status changes to `PICKED_UP`. A 14-day review window opens.**

---

### Step 16: Create Reviews (Bidirectional)

#### Car Owner Reviews Workshop

```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <CAR_OWNER_TOKEN>" \
  -d '{
    "serviceBookingId": 1,
    "rating": 5,
    "comment": "Excellent service! Very professional and transparent with pricing."
  }'
```

#### Workshop Reviews Car Owner

```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <WORKSHOP_MANAGER_TOKEN>" \
  -d '{
    "serviceBookingId": 1,
    "rating": 5,
    "comment": "Great customer. Vehicle was clean and on time."
  }'
```

**Review Rating Values:** 1-5 stars

---

## Endpoint Reference

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/users/signup` | Register user |
| POST | `/api/v1/users/signin` | Login |
| GET | `/api/v1/workshops/search` | Search workshops |
| GET | `/api/v1/catalog/brands` | Get vehicle brands |
| GET | `/api/v1/catalog/brands/{id}/models` | Get brand models |
| GET | `/api/v1/catalog/services` | Get service catalog |
| GET | `/api/v1/catalog/capability-tags` | Get capability tags |

### IAM Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/users/me` | Yes | Current user profile |
| PUT | `/api/v1/users/me` | Yes | Update profile |
| POST | `/api/v1/users/forgot-password` | No | Request password reset |

### Workshop Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/workshops` | WORKSHOP_MANAGER | Create workshop |
| GET | `/api/v1/workshops/my-workshop` | WORKSHOP_MANAGER | Get my workshop |
| POST | `/api/v1/workshops/locations` | WORKSHOP_MANAGER | Add location |
| POST | `/api/v1/workshops/service-templates` | WORKSHOP_MANAGER | Add service |
| POST | `/api/v1/workshops/tags` | WORKSHOP_MANAGER | Add capability tag |
| GET | `/api/v1/workshops/my-workshop/available-requests` | WORKSHOP_MANAGER | View requests |

### Vehicle Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/vehicles` | CAR_OWNER | Register vehicle |
| GET | `/api/v1/vehicles` | CAR_OWNER | List my vehicles |
| PUT | `/api/v1/vehicles/{id}/mileage` | CAR_OWNER | Update mileage |
| POST | `/api/v1/vehicles/{id}/maintenances` | CAR_OWNER | Add maintenance record |
| GET | `/api/v1/vehicles/{id}/maintenances` | CAR_OWNER | Get maintenance history |

### Matching Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/matching/workshops` | CAR_OWNER | Search workshops |
| POST | `/api/service-requests` | CAR_OWNER | Create service request |
| GET | `/api/service-requests` | CAR_OWNER | List my requests |
| DELETE | `/api/service-requests/{id}` | CAR_OWNER | Cancel request |

### Offer Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/offers` | WORKSHOP | Create offer |
| POST | `/api/offers/{id}/accept` | CAR_OWNER | Accept offer |
| POST | `/api/offers/{id}/reject` | CAR_OWNER | Reject offer |
| POST | `/api/offers/{id}/withdraw` | WORKSHOP | Withdraw offer |

### Booking Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/service-bookings/{id}/confirm-schedule` | CAR_OWNER | Confirm schedule |
| POST | `/api/service-bookings/{id}/complete` | WORKSHOP | Mark complete |
| POST | `/api/service-bookings/{id}/confirm-pickup` | CAR_OWNER | Confirm pickup |
| DELETE | `/api/service-bookings/{id}` | CAR_OWNER | Cancel booking |

### Review Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/reviews` | Yes | Create review |
| GET | `/api/reviews/window-status` | Yes | Check review window |
| POST | `/api/reviews/{id}/report` | Yes | Report review |

---

## Common Issues

### 1. Service Request Returns Empty Matches

**Cause:** No workshops with locations near the search radius.

**Solution:** Ensure the workshop has at least one location with valid lat/long coordinates.

### 2. Cannot Accept Offer

**Cause:** Offer may be expired or already accepted/rejected.

**Solution:** Check offer status - only `PENDING` offers can be accepted.

### 3. Review Window Expired

**Cause:** Review window is 14 days after `PICKED_UP` status.

**Solution:** Check `GET /api/reviews/window-status?serviceBookingId=X` to see remaining time.

### 4. Workshop Not Appearing in Search

**Causes:**
- No location added to workshop
- Workshop has no service templates
- Workshop is too far from search location
- Workshop has no active subscription (for premium matching)

### 5. Invalid Brand ID

**Cause:** The brandId must exist in the catalog.

**Solution:** First call `GET /api/v1/catalog/brands` to get valid brand IDs.

### 6. JWT Token Expired

**Cause:** Tokens expire after 7 days (dev configuration).

**Solution:** Re-login to get a new token: `POST /api/v1/users/signin`

---

## Testing Checklist

- [ ] Register CAR_OWNER and WORKSHOP_MANAGER users
- [ ] Login both users and save tokens
- [ ] Create workshop as WORKSHOP_MANAGER
- [ ] Add workshop location with coordinates
- [ ] Add at least one service template
- [ ] Register vehicle as CAR_OWNER
- [ ] Search workshops from CAR_OWNER perspective
- [ ] Create service request
- [ ] Accept offer and complete booking flow
- [ ] Verify reviews work (both directions)
- [ ] Check Swagger UI at `/swagger-ui.html`