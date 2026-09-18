# Cosmetics E-Commerce Backend

Spring Boot REST API for a cosmetics catalog, customer accounts, cart, checkout, orders, reviews, inventory, coupons, and admin operations.

## Run & Operate

- The managed API workflow runs `cd spring-app && mvn spring-boot:run` from `artifacts/api-server` (port 8080, routed at `/api`)
- `cd artifacts/api-server/spring-app && mvn test` — run the Java unit and integration tests
- `cd artifacts/api-server/spring-app && mvn -q -DskipTests package` — build the Spring Boot jar
- `pnpm run typecheck` — full typecheck across all packages
- `pnpm run build` — typecheck + build all packages
- `pnpm --filter @workspace/api-spec run codegen` — regenerate API hooks and Zod schemas from the OpenAPI spec
- `pnpm --filter @workspace/db run push` — push DB schema changes (dev only)
- Required env: `JWT_SECRET` or the platform `SESSION_SECRET`; MySQL can be configured with `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`

## Stack

- pnpm workspaces, Node.js 24, TypeScript 5.9
- API: Java 21-compatible Spring Boot 3.5
- DB: MySQL via Spring Data JPA/Hibernate, with H2 as the local fallback
- Security: JWT access/refresh tokens, BCrypt, method-level role authorization
- Documentation: Springdoc OpenAPI/Swagger

## Where things live

- `artifacts/api-server/spring-app/src/main/java/com/cosmetics/ecommerce/entity` — JPA domain model
- `artifacts/api-server/spring-app/src/main/java/com/cosmetics/ecommerce/service` — business logic and transactions
- `artifacts/api-server/spring-app/src/main/java/com/cosmetics/ecommerce/controller` — REST resources
- `artifacts/api-server/spring-app/src/main/resources/application.yml` — environment-driven runtime configuration
- `artifacts/api-server/spring-app/README.md` — setup, environment variables, and endpoint summary

## Architecture decisions

- The API uses `/api` as the servlet context path so proxy routing and Swagger share the same public base path.
- H2 is only a local fallback; setting the DB environment variables uses MySQL without code changes.
- Checkout snapshots order item names/prices and decrements stock inside one transaction.
- Payment is behind `PaymentGateway`; the included mock adapter is safe for development and stores no card data.

## Product

- Customers can register, authenticate, browse/search products, manage addresses/cart/wishlist, apply coupons, place and cancel orders, and review delivered products.
- Admin/staff roles can manage the catalog, stock, orders, reviews, coupons, and dashboard statistics.

## User preferences

_No additional user preferences recorded._

## Gotchas

- Set a 32+ character `JWT_SECRET` or use the available `SESSION_SECRET` fallback before starting the service.
- Enable development seed data only with `SEED_DATA=true` and `SEED_DEFAULT_PASSWORD` set.

## Pointers

- See the `pnpm-workspace` skill for workspace structure, TypeScript setup, and package details
