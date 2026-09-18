# Cosmetics E-Commerce Backend

Production-oriented REST API for a cosmetics and beauty products store. The project uses Spring Boot, Spring Data JPA, Spring Security with JWT, MySQL, Bean Validation, OpenAPI, and a payment gateway abstraction with a development mock gateway.

## Current capabilities

- Customer, staff, and admin roles with BCrypt password hashing and JWT access/refresh tokens
- Public product, category, and brand browsing with pagination, sorting, search, filters, slugs, and featured products
- Admin product, category, brand, coupon, review, stock, and order-status management
- Customer addresses, cart quantity/stock validation, wishlist-ready data model, checkout, coupon validation, mock payments, and order cancellation
- Transactional checkout that snapshots product names/prices, reserves stock, creates a payment record, and clears the cart
- Consistent success envelopes and global error responses
- OpenAPI/Swagger UI with Bearer JWT support
- Optional safe development seeding

## Requirements

- Java 21 for the target deployment (the Replit-managed toolchain currently compiles this source with Java 19; no Java-21-only language APIs are used)
- Maven 3.8+
- MySQL 8+

## Environment variables

| Variable | Required | Description |
| --- | --- | --- |
| `DB_URL` | No | Defaults to local `cosmetics_ecommerce` MySQL database |
| `DB_USERNAME` | No | MySQL username, defaults to `root` |
| `DB_PASSWORD` | No | MySQL password |
| `JWT_SECRET` | Yes | At least 32 characters; `SESSION_SECRET` is accepted as a platform fallback |
| `PORT` | No | Defaults to `8080` |
| `DDL_AUTO` | No | Defaults to `update`; use `validate` after migrations are established |
| `CORS_ALLOWED_ORIGINS` | No | Comma-separated frontend origins |
| `MAIL_ENABLED` | No | Enables welcome, password-reset, and order-confirmation mail, defaults to `false` |
| `MAIL_HOST` / `MAIL_PORT` | No | SMTP host and port |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | No | SMTP credentials |
| `MAIL_FROM` | No | Sender address |
| `FRONTEND_RESET_URL` | No | Frontend password-reset URL used in reset mail |
| `SPRING_PROFILE` | No | Spring profile name |
| `SEED_DATA` | No | Set to `true` to enable development seed data |
| `SEED_DEFAULT_PASSWORD` | No | Required when `SEED_DATA=true`; use a development-only password of at least 8 characters |

Create the MySQL database before starting if `createDatabaseIfNotExist` is disabled in your environment:

```sql
CREATE DATABASE cosmetics_ecommerce CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## Run locally

```bash
export JWT_SECRET='replace-with-a-long-random-development-secret'
export DB_URL='jdbc:mysql://localhost:3306/cosmetics_ecommerce?serverTimezone=UTC'
mvn spring-boot:run
```

The service is mounted under `/api`:

- Health: `GET /api/healthz`
- Swagger UI: `/api/swagger-ui.html`
- OpenAPI JSON: `/api/v3/api-docs`

For seeded development data:

```bash
export SEED_DATA=true
export SEED_DEFAULT_PASSWORD='replace-with-a-development-password'
mvn spring-boot:run
```

Seeded development accounts use that password:

- `admin@cosmetics.local`
- `staff@cosmetics.local`
- `maya@cosmetics.local`
- `noah@cosmetics.local`
- `lina@cosmetics.local`

## Authentication

1. `POST /api/auth/register` or `POST /api/auth/login`
2. Send the returned access token as `Authorization: Bearer <token>`
3. Use `POST /api/auth/refresh-token` with the refresh token when the access token expires
4. Use `GET /api/auth/me` to retrieve the current safe user profile

Passwords, JWT values, and payment credentials are never logged or returned from the API.

## Key endpoints

| Area | Endpoints |
| --- | --- |
| Auth | `/api/auth/register`, `/api/auth/login`, `/api/auth/refresh-token`, `/api/auth/me` |
| Catalog | `GET /api/products`, `/api/products/{id}`, `/api/products/slug/{slug}`, `/api/categories`, `/api/brands` |
| Admin catalog | `/api/products`, `/api/admin/categories`, `/api/admin/brands` |
| Cart | `/api/cart`, `/api/cart/items`, `/api/cart/items/{itemId}`, `/api/cart/clear` |
| Addresses | `/api/addresses`, `/api/addresses/{id}` |
| Orders | `/api/orders`, `/api/orders/{id}`, `/api/orders/{id}/cancel` |
| Coupons | `/api/coupons/apply`, `/api/admin/coupons` |
| Reviews | `/api/products/{productId}/reviews`, `/api/admin/reviews/{id}/approve` |
| Admin | `/api/admin/dashboard`, `/api/admin/orders/{id}/status` |

## Example requests

```json
POST /api/auth/register
{
  "firstName": "Maya",
  "lastName": "Perera",
  "email": "maya@example.com",
  "password": "correct-horse-battery-staple",
  "phoneNumber": "+94110000000"
}
```

```json
POST /api/cart/items
{
  "productId": 1,
  "quantity": 2
}
```

```json
POST /api/orders
{
  "shippingAddressId": 1,
  "paymentMethod": "MOCK",
  "couponCode": "WELCOME10"
}
```

The mock payment gateway is intentionally isolated behind `PaymentGateway`; a Razorpay or Stripe adapter can be added without changing checkout orchestration or order persistence. Card data is never stored. H2 is only the no-configuration local fallback; setting `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` switches the same application to MySQL.