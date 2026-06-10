# Autonexo Backend

Spring Boot REST API for the **Autonexo** platform (workshop, vehicle, and identity management).

This document is focused on the **public HTTP contract** so the frontend team can integrate against a stable, predictable API.

---

## Table of Contents

1. [Tech Stack](#tech-stack)
2. [Base URL & Versioning](#base-url--versioning)
3. [Response Shapes](#response-shapes)
4. [Error Handling](#error-handling)
5. [Authentication](#authentication)
6. [IAM Endpoints](#iam-endpoints)
7. [Frontend Integration Tips](#frontend-integration-tips)

---

## Tech Stack

- **Java 25** · Spring Boot 3 · Spring Security (JWT bearer)
- **JPA / Hibernate** · MySQL (prod) · H2 (test)
- **Maven** build

---

## Base URL & Versioning

All endpoints are prefixed with `/api/v1`.

```
http://localhost:8080/api/v1
```

OpenAPI/Swagger UI is exposed at `/swagger-ui.html` in non-production profiles.

---

## Response Shapes

The API uses **three** response shapes consistently. The frontend can rely on always parsing JSON — there are no plain-text bodies.

### 1. Success — Resource

For endpoints that return domain data (`UserResource`, `WorkshopResource`, etc.):

```json
{
  "id": 42,
  "email": "jane@example.com",
  "firstName": "Jane",
  "lastName": "Doe",
  "phoneNumber": "+51999888777",
  "active": true,
  "verified": true
}
```

### 2. Success — Authentication

`POST /api/v1/users/signin` returns a JWT bundle:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 604800,
  "user": { "id": 42, "email": "jane@example.com", "...": "..." }
}
```

`expiresIn` is in **seconds** (currently `604800` = 7 days).

### 3. Success — Message

For endpoints that only need to confirm an action (`signup`, `change-password`, `verify-email`, etc.):

```json
{
  "message": "User registered successfully"
}
```

> The frontend should always read `body.message` instead of treating the body as a plain string.

---

## Error Handling

### Error Response Schema

**Every** error response — whether produced by the controller, the global handler, an `@ControllerAdvice`, or Spring Security — has the same JSON shape:

```json
{
  "timestamp": "2026-06-09T12:34:56.789",
  "status":    409,
  "error":     "Conflict",
  "errorCode": "EMAIL_ALREADY_EXISTS",
  "message":   "An account with this email already exists",
  "path":      "/api/v1/users/signup"
}
```

| Field        | Type     | Description                                                                                 |
| ------------ | -------- | ------------------------------------------------------------------------------------------- |
| `timestamp`  | string   | ISO-8601 local time the error was produced.                                                 |
| `status`     | int      | HTTP status code (e.g. `400`, `401`, `404`, `409`, `500`).                                  |
| `error`      | string   | Standard HTTP reason phrase (e.g. `"Bad Request"`, `"Conflict"`).                          |
| `errorCode`  | string   | **Machine-readable code** (see [Error Codes](#error-codes)). Use this for i18n / branching. |
| `message`    | string   | **User-friendly, safe-to-display** message.                                                 |
| `path`       | string   | Request URI that produced the error.                                                        |

### Error Codes

| `errorCode`             | HTTP | Meaning                                                          |
| ----------------------- | ---- | ---------------------------------------------------------------- |
| `USER_NOT_FOUND`        | 404  | No user matches the requested identifier.                        |
| `INVALID_CREDENTIALS`   | 401  | Email or password is wrong.                                      |
| `EMAIL_ALREADY_EXISTS`  | 409  | Signup attempted with an email that is already registered.       |
| `ACCOUNT_DEACTIVATED`   | 403  | The account exists but is deactivated.                           |
| `UNAUTHORIZED`          | 401  | Missing or invalid authentication token.                         |
| `ACCESS_DENIED`         | 403  | Authenticated user lacks permission.                             |
| `VALIDATION_ERROR`      | 400  | Request payload failed `@Valid` validation.                      |
| `INVALID_TOKEN`         | 400  | Verification or password-reset token is invalid or expired.      |
| `INTERNAL_ERROR`        | 500  | Unexpected server error. The user-friendly message is generic.  |

> **Frontend tip:** Switch on `errorCode` (not on `message` or `status`) for internationalization and analytics. The `message` is for direct display but its wording may evolve.

### Validation Errors

`@Valid` failures return `400` with `errorCode = "VALIDATION_ERROR"`. The first field-level error is reported in `message`:

```json
{
  "timestamp": "...",
  "status": 400,
  "error": "Bad Request",
  "errorCode": "VALIDATION_ERROR",
  "message": "email: must be a well-formed email address",
  "path": "/api/v1/users/signup"
}
```

> The current implementation only surfaces the **first** validation error. If you need all field-level errors surfaced as an array, that is a follow-up enhancement.

### Security Errors (401 / 403 from Spring Security)

Two paths lead to auth/security errors:

1. **No/bad JWT** → `UnauthorizedRequestHandlerEntryPoint` → `401 UNAUTHORIZED`
2. **JWT ok but insufficient role** → `UnauthorizedRequestHandlerAccessDenied` → `403 ACCESS_DENIED`

Both return the same `ErrorResponse` JSON shape, so the frontend can use one parser.

---

## Authentication

All non-public endpoints require a `Bearer` JWT in the `Authorization` header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Public endpoints (no auth required):

- `POST /api/v1/users/signup`
- `POST /api/v1/users/signin`
- `GET  /api/v1/users/available-roles`
- `POST /api/v1/users/forgot-password`
- `POST /api/v1/users/reset-password`
- `GET  /api/v1/workshops/catalog/**`
- `GET  /api/v1/workshops/search`
- `GET  /api/v1/workshops/*/public`
- Swagger / actuator (health/info/metrics)

---

## IAM Endpoints

Base path: `/api/v1/users`

| Method | Path                          | Auth | Description                                       | Success     | Possible Errors                                                       |
| ------ | ----------------------------- | ---- | ------------------------------------------------- | ----------- | --------------------------------------------------------------------- |
| POST   | `/signup`                     | ❌   | Register a new user                               | `201` + `MessageResponse` | `400 VALIDATION_ERROR` · `409 EMAIL_ALREADY_EXISTS`                   |
| POST   | `/signin`                     | ❌   | Authenticate and obtain a JWT                     | `200` + `AuthenticationResponseResource` | `400 VALIDATION_ERROR` · `401 INVALID_CREDENTIALS` · `403 ACCOUNT_DEACTIVATED` |
| GET    | `/available-roles`            | ❌   | List roles available during registration          | `200` array | —                                                                     |
| POST   | `/forgot-password`            | ❌   | Request a password-reset email                    | `200` + `MessageResponse` | — *(always 200 to prevent email enumeration)*                         |
| POST   | `/reset-password`             | ❌   | Reset password using token from email             | `200` + `MessageResponse` | `400 INVALID_TOKEN`                                                   |
| POST   | `/resend-verification`        | ❌   | Resend email-verification token                   | `200` + `MessageResponse` | `404 USER_NOT_FOUND`                                                  |
| POST   | `/verify-email`               | ❌   | Confirm email with token                          | `200` + `MessageResponse` | `400 INVALID_TOKEN`                                                   |
| GET    | `/me`                         | ✅   | Get current authenticated user                    | `200` + `UserResource` | `401 UNAUTHORIZED` · `404 USER_NOT_FOUND` · `403 ACCOUNT_DEACTIVATED` |
| PUT    | `/me`                         | ✅   | Update current user's profile                     | `200` + `UserResource` | `401 UNAUTHORIZED` · `404 USER_NOT_FOUND` · `403 ACCOUNT_DEACTIVATED` |
| PUT    | `/me/password`                | ✅   | Change current password                           | `200` + `MessageResponse` | `400 VALIDATION_ERROR` · `401 INVALID_CREDENTIALS` · `403 ACCOUNT_DEACTIVATED` |
| DELETE | `/me`                         | ✅   | Deactivate current account                        | `200` + `MessageResponse` | `401 UNAUTHORIZED` · `404 USER_NOT_FOUND`                             |
| GET    | `/by-email?email=…`           | ✅   | Look up user by email                             | `200` + `UserResource` | `404 USER_NOT_FOUND`                                                  |
| GET    | `/`                           | ✅   | List all users (admin-ish)                        | `200` array | —                                                                     |
| GET    | `/verification-status?email=…`| ❌   | Check whether an email is verified                | `200` `{email, verified}` | `404 USER_NOT_FOUND`                                                  |

### Example: Successful signup

**Request**

```http
POST /api/v1/users/signup
Content-Type: application/json

{
  "email": "jane@example.com",
  "password": "S3cure!pass",
  "firstName": "Jane",
  "lastName": "Doe",
  "phoneNumber": "+51999888777",
  "requestedRole": "CAR_OWNER"
}
```

**Response — `201 Created`**

```json
{ "message": "User registered successfully" }
```

### Example: Email already taken

**Response — `409 Conflict`**

```json
{
  "timestamp": "2026-06-09T12:34:56.789",
  "status": 409,
  "error": "Conflict",
  "errorCode": "EMAIL_ALREADY_EXISTS",
  "message": "An account with this email already exists",
  "path": "/api/v1/users/signup"
}
```

### Example: Invalid credentials on signin

**Response — `401 Unauthorized`**

```json
{
  "timestamp": "2026-06-09T12:34:56.789",
  "status": 401,
  "error": "Unauthorized",
  "errorCode": "INVALID_CREDENTIALS",
  "message": "Invalid email or password",
  "path": "/api/v1/users/signin"
}
```

### Example: Unauthenticated request to a protected endpoint

**Response — `401 Unauthorized`** *(from Spring Security entry point)*

```json
{
  "timestamp": "2026-06-09T12:34:56.789",
  "status": 401,
  "error": "Unauthorized",
  "errorCode": "UNAUTHORIZED",
  "message": "Authentication required",
  "path": "/api/v1/users/me"
}
```

---

## Frontend Integration Tips

1. **Single error parser.** Always parse the response as `ErrorResponse`. Switch on `errorCode` to decide what to do.
2. **HTTP status is reliable.** Pair `status` and `errorCode`:
   - `401 UNAUTHORIZED` or `401 INVALID_CREDENTIALS` → redirect to login.
   - `403 ACCESS_DENIED` or `403 ACCOUNT_DEACTIVATED` → show "no permission" UI.
   - `404 USER_NOT_FOUND` → show "not found" UI.
   - `409 EMAIL_ALREADY_EXISTS` → prompt user to sign in instead.
   - `400 VALIDATION_ERROR` / `400 INVALID_TOKEN` → display `message` inline on the form.
   - `500 INTERNAL_ERROR` → generic retry banner.
3. **Treat `message` as i18n-ready.** If you ship multiple languages, map `errorCode → localized string` on the frontend rather than translating the server's `message` directly.
4. **Read success bodies as JSON too.** All `2xx` bodies are JSON objects (never plain strings). `success` and `error` both follow the same parsing rules.
5. **Don't parse `error` or `status` to drive UX.** Use `errorCode`. `status` is informational; the `errorCode` is the contract.
6. **Forgot password is intentionally opaque.** It always returns `200` regardless of whether the email exists, to prevent account enumeration. Surface a generic "check your inbox" message; do not try to distinguish "email sent" from "email not found".

---

## Versioning & Compatibility

- All breaking changes must be introduced under a new path prefix (`/api/v2/...`).
- Adding a new field to `ErrorResponse` is non-breaking; the frontend should ignore unknown fields.
- Renaming an `errorCode` value is breaking — coordinate with the frontend team first.
