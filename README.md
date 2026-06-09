# CompusLink

A student-focused platform built as a **Spring Boot microservices** backend with a
**React + TypeScript** frontend. Students can trade items (marketplace), find
flatmates (colocation), browse job/internship offers, discover events, and message
each other — all behind a single API gateway with JWT authentication.

> This README documents the two active parts of the repository:
> [`compuslink-microservices/`](compuslink-microservices) (backend) and
> [`campuslink-frontend/`](campuslink-frontend) (frontend). The `CompusLink/`
> directory is a separate/legacy codebase and is not covered here.

---

## Table of contents

1. [Running locally](#1-running-locally)
2. [Architecture overview](#2-architecture-overview)
3. [Repository layout](#3-repository-layout)
4. [The project in detail](#4-the-project-in-detail)
   - [4.1 Infrastructure](#41-infrastructure-eureka--gateway--common-lib)
   - [4.2 Microservices](#42-the-microservices)
   - [4.3 Cross-cutting concerns](#43-cross-cutting-concerns)
   - [4.4 Frontend](#44-frontend)
5. [Deployment](#5-deployment)
6. [Port & service reference](#6-port--service-reference)

---

## 1. Running locally

### Prerequisites

| Tool | Version | Used for |
|---|---|---|
| **JDK** | 21 | Building/running the Spring Boot services |
| **Maven** | 3.9+ | Building the backend (no wrapper is committed) |
| **Node.js** | 20+ | Building/running the frontend |
| **Docker + Docker Compose** | recent | The easiest way to run the whole stack |
| **PostgreSQL** | 16 | Only if you run services without Docker |

The stack is **Spring Boot 4.0.x**, **Spring Cloud 2025.1.x**, **Java 21**,
**React 19**, **Vite 8**, **Tailwind 4**.

### Option A — Docker Compose (recommended)

> ⚠️ **Important:** the service `Dockerfile`s do `COPY target/*.jar app.jar` — they
> bundle a **pre-built** JAR rather than compiling inside the image. You must run a
> Maven build **first**, otherwise Compose will package stale or missing JARs.

```bash
cd compuslink-microservices

# 1. Build every service JAR (also installs common-lib into the reactor)
mvn clean package -DskipTests

# 2. Build images and start the whole backend (Postgres, Eureka, gateway, services)
docker compose up -d --build

# 3. Watch them register (all services should show UP)
#    Eureka dashboard:
open http://localhost:8761
```

Whenever you change a backend service, rebuild that one:

```bash
mvn -pl <service> -am clean package -DskipTests
docker compose up -d --build <service>     # e.g. colocation-service
```

Then start the frontend (Vite dev server, hot-reloading):

```bash
cd campuslink-frontend
npm install
npm run dev          # http://localhost:5173
```

The frontend's dev API base defaults to `http://localhost:8080` (the gateway) — see
[`src/services/api.ts`](campuslink-frontend/src/services/api.ts). The gateway's
`FRONTEND_URL` is set to `http://localhost:5173` for CORS.

### Option B — Run services manually (no Docker for the backend)

1. **Postgres** — start it and create the seven databases (see
   [`init-db.sql`](compuslink-microservices/init-db.sql)):
   `compuslink_users`, `_marketplace`, `_colocation`, `_offers`, `_events`,
   `_messaging`, `_common`. The compose default credentials are user **`mourad`**
   with an **empty password** (`POSTGRES_HOST_AUTH_METHOD=trust`).
2. **Eureka** must start first:
   ```bash
   cd compuslink-microservices/eureka-server && mvn spring-boot:run   # :8761
   ```
3. **Then the gateway and each service** (any order once Eureka is up):
   ```bash
   cd ../api-gateway       && mvn spring-boot:run     # :8080
   cd ../user-service      && mvn spring-boot:run     # :8081
   cd ../marketplace-service && mvn spring-boot:run   # :8082
   # …colocation :8083, offer :8084, event :8085, messaging :8086, common :8087
   ```
   Each service reads `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`,
   `EUREKA_URI` from the environment (sensible localhost defaults are baked into
   each `application.properties`).
4. **Frontend**: `npm run dev` as above.

### Default URLs once running

| What | URL |
|---|---|
| Frontend | http://localhost:5173 |
| API gateway (all `/api/**` calls) | http://localhost:8080 |
| Eureka dashboard | http://localhost:8761 |
| Postgres | `localhost:5432` (user `mourad`, no password) |

### First steps in the app

Register a user at `/api/auth/register` (or via the frontend's auth page). The first
token lets you create items, colocation posts, offers, events, and message other
users. Google OAuth2 login is also wired up in `user-service` (requires Google client
credentials to be configured).

---

## 2. Architecture overview

```
                           ┌─────────────────────────┐
   Browser  ──HTTPS──►     │   React + Vite frontend  │
                           └────────────┬────────────┘
                                        │  /api/**  (Bearer JWT)
                                        ▼
                           ┌─────────────────────────┐
                           │      API Gateway :8080   │  ← validates JWT,
                           │  (Spring Cloud Gateway)  │    injects X-User-Id,
                           └────────────┬────────────┘    CORS, routing
                                        │  lb://<service>  (Eureka load-balanced)
        ┌───────────────┬──────────────┼──────────────┬───────────────┐
        ▼               ▼              ▼              ▼               ▼
   user-service   marketplace    colocation       offer          event /
     :8081          :8082          :8083          :8084        messaging /
                                                                 common
        │               │              │              │               │
        └───────────────┴──────────────┴──────────────┴───────────────┘
                                        │   each owns its own database
                                        ▼
                           ┌─────────────────────────┐
                           │   PostgreSQL  (7 DBs)    │
                           └─────────────────────────┘

   All services register with ►  Eureka Server :8761  ◄ (service discovery)
   Service-to-service calls use Feign clients resolved by service name.
```

**Key principles**

- **Database-per-service**: each microservice owns a separate Postgres database and
  never reaches into another's tables. Cross-service data (e.g. a seller's name) is
  fetched at runtime over HTTP via **Feign**.
- **Stateless services + gateway-enforced auth**: only the gateway validates JWTs.
  Downstream services trust the `X-User-Id` header the gateway injects.
- **Service discovery**: services register with **Eureka**; the gateway and Feign
  clients address each other by logical name (`lb://user-service`,
  `@FeignClient(name="user-service")`), not hardcoded hosts.
- **Shared library**: `common-lib` holds DTOs, exceptions, the global error handler,
  and JWT utilities reused by every service.

---

## 3. Repository layout

```
development-platform-compuslink/
├── compuslink-microservices/          # Spring Boot backend (Maven multi-module)
│   ├── pom.xml                         # parent POM (Java 21, Spring Boot 4, Spring Cloud)
│   ├── docker-compose.yml              # full local stack
│   ├── init-db.sql                     # creates the 7 databases
│   ├── common-lib/                     # shared DTOs, exceptions, JwtUtil  (a library, not a service)
│   ├── eureka-server/                  # service registry            :8761
│   ├── api-gateway/                    # edge router + JWT filter     :8080
│   ├── user-service/                   # auth, profiles, CVs          :8081
│   ├── marketplace-service/            # items for sale               :8082
│   ├── colocation-service/             # flatmate posts               :8083
│   ├── offer-service/                  # jobs/internships + apps      :8084
│   ├── event-service/                  # events + participation       :8085
│   ├── messaging-service/              # 1-to-1 conversations         :8086
│   ├── common-service/                 # reports + saved items        :8087
│   └── k8s/                            # Kubernetes manifests (k3s)
├── campuslink-frontend/                # React + TypeScript + Vite + Tailwind
│   └── src/{pages,components,context,services,data,assets}
├── Jenkinsfile                         # CI/CD pipeline
├── terraform/                          # infrastructure as code
└── README.md                           # this file
```

Every service follows the same internal package shape:
`model/` (JPA entities + enums) · `repository/` (Spring Data) · `service/` (business
logic) · `controller/` (REST) · `dto/` (request/response) · `client/` (Feign) ·
`config/` (web/security).

---

## 4. The project in detail

### 4.1 Infrastructure: Eureka · Gateway · common-lib

#### Eureka Server (`:8761`)
A Spring Cloud Netflix Eureka registry. Every other service registers itself on
startup and discovers peers through it. Services use
`eureka.instance.prefer-ip-address=true` so they register a routable IP (essential
on Kubernetes, where pod hostnames aren't DNS-resolvable between pods).

#### API Gateway (`:8080`) — Spring Cloud Gateway (reactive/WebFlux)
The single entry point for the browser. Three concerns:

- **Routing** (`application.yml`): path predicates map to load-balanced services, e.g.
  - `/api/auth/**, /api/users/**, /api/me/**, /oauth2/**` → `user-service`
  - `/api/items/**, /api/marketplace/**` → `marketplace-service`
  - `/api/coloc/**` → `colocation-service`
  - `/api/offers/**, /api/applications/**` → `offer-service`
  - `/api/events/**` → `event-service`
  - `/api/messages/**, /api/conversations/**` → `messaging-service`
  - `/api/reports/**, /api/saved/**` → `common-service`
  - plus static upload paths (`/uploads/**`, `/coloc-uploads/**`, `/event-uploads/**`,
    `/user-uploads/**`) routed to the owning service.
- **Authentication** (`filter/JwtAuthFilter`, a `GlobalFilter`): for every request it
  - lets **public paths** through unauthenticated (`/api/auth/login`, `/register`,
    `/refresh`, OAuth2 endpoints, and the upload paths);
  - otherwise reads the `Authorization: Bearer …` header, validates the JWT, and on
    success **mutates the request to add an `X-User-Id` header** carrying the user's
    UUID before forwarding;
  - returns **401 Unauthorized** when the token is missing/invalid.
- **CORS** (`config/CorsConfig`): allows the frontend origin (`FRONTEND_URL`) with
  credentials; a `DedupeResponseHeader` default-filter prevents duplicate CORS headers.

#### common-lib (shared library, not a running service)
Packaged as a dependency and component-scanned by every service
(`scanBasePackages = {"…service", "com.compuslink.common"}`):

- **`dto/UserSummaryDTO`** — `id, fullName, email, profilePicUrl`; the shape returned
  by user-service's internal endpoint and consumed by other services' Feign clients.
- **`exception/`** — typed exceptions (`EntityNotFoundException`,
  `AccessDeniedException`, `DuplicateResourceException`, `BusinessRuleException`) and a
  **`GlobalExceptionHandler`** that maps them to HTTP statuses:
  | Exception | HTTP |
  |---|---|
  | `EntityNotFoundException` | 404 Not Found |
  | `AccessDeniedException` | 403 Forbidden |
  | `DuplicateResourceException` | 409 Conflict |
  | `BusinessRuleException` | 400 Bad Request |
  Responses use a consistent `ApiErrorResponse` body.
- **`security/JwtUtil`** — JWT signing/parsing helpers shared by user-service (issuing)
  and the gateway (validating).

### 4.2 The microservices

#### user-service (`:8081`) — identity, profiles, CVs
The only service that issues tokens.

- **Entities**: `Users` (email, `passwordHash`, `refreshTokenHash`, `role`
  (`UserRole` enum), `fullName`, `university`, `city`, `phoneNumber`, `bio`,
  `profilePicUrl`, `cvUrl`, `isActive`, `isVerified`, timestamps); `Cv` (uploaded CV
  files with a default flag); `UserPrincipal` (Spring Security adapter).
- **`AuthController`** `/api/auth`: `register`, `login`, `refresh-token`, `logout`.
  Login returns an access token + refresh token; the refresh token's hash is stored on
  the user row so it can be rotated/revoked.
- **`MeController`** `/api/me`: the authenticated user's own profile —
  `GET/PATCH /profile`, profile-picture upload/delete, and CV management
  (`/profile/cvs`, upload `/profile/cv`, delete, set-default).
- **`UserController`**: `GET /api/users/{id}/public` (public profile) plus the
  **internal** endpoints `GET /internal/users/{id}/summary` and `/exists` consumed by
  other services via Feign.
- **OAuth2**: `config/SecurityConfig` + `OAuth2SuccessHandler` implement Google
  login — on success the handler issues the app's own JWT and redirects to the
  frontend callback.

#### marketplace-service (`:8082`) — items for sale
Classified-ad listings with photos and buyer interest.

- **Entities**: `Item` (seller, title, description, `price`, city, `condition`
  (`ItemCondition`: NEW/LIKE_NEW/GOOD/FAIR), category, `status` (`ItemStatus`:
  OPEN/SOLD/CLOSED), timestamps); `ItemImage` (file URL, sort order, cover flag);
  `ItemInterest` (buyer + optional message, unique per `(item,user)`).
- **`ItemController`** `/api/items`: create (multipart), browse (dynamic filters via
  `ItemSpecification`), detail, partial update + image add/remove, mark-sold,
  close (soft delete), express-interest, list-interests (seller-only), my-items.
- **`FileStorageService`** stores photos on disk and returns public `/uploads/**` URLs
  served by `WebConfig`. Seller/buyer names are resolved via the `UserClient` Feign
  call, degrading to `"Unknown"` if user-service is unavailable.
- See [`Marketplace-and-Messaging-Explained.md`](Marketplace-and-Messaging-Explained.md)
  on the Desktop for a class-by-class deep dive (if present).

#### colocation-service (`:8083`) — flatmate posts
- **Entities**: `ColocPost` (poster, title, description, city, address, start date,
  `spotsNeeded`/`spotsConfirmed`, `housingType`, `rentPerPerson`, `furnished`,
  `status`, cover URL, `isBlocked`/`blockedAt`); `ColocImage`; `ColocAmenity`;
  `ColocInterest` (an applicant + message + status PENDING/ACCEPTED/REJECTED);
  `ColocMessage` (per-interest chat).
- **`ColocPostController`** `/api/coloc`: CRUD + photo upload, browse with filters,
  express-interest, accept/reject, list interested people (poster-only),
  "my-interests", block/unblock a post, and an internal `exists` check.
- The frontend's express-interest flow also opens a thread in **messaging-service**, so
  applicants and posters can chat in the central inbox.

#### offer-service (`:8084`) — jobs / internships + applications
- **Entities**: `Offer` (poster, `type` (JOB/INTERNSHIP/PFE), title, company, city,
  `locationType` (ON_SITE/REMOTE/HYBRID), `experienceLevel` (STUDENT/JUNIOR/SENIOR),
  duration, description, `status` (OPEN/CLOSED)); `Application`
  (`status`: PENDING/SEEN/ACCEPTED/REJECTED).
- **`OfferController`** `/api/offers`: create, browse, my-offers, detail, close,
  delete. **`ApplicationController`**: apply to an offer, list an offer's applications
  (poster-only), `mine`, and update an application's status. Internal `exists` endpoint.

#### event-service (`:8085`) — campus events
- **Entities**: `Event` (organizer, title, description, location, city, `eventDate`,
  `category` (TECH/CAREER/SOCIAL/SPORT/CULTURE/WORKSHOP/OTHER), `maxParticipants`,
  cover URL); `EventParticipant`.
- **`EventController`** `/api/events`: create, browse, detail, cover upload,
  join/leave, cancel, delete, my-events, my-participations, list participants.

#### messaging-service (`:8086`) — 1-to-1 conversations
- **Entities**: `Conversation` (two participant UUIDs, `lastMessageAt`); `Message`
  (sender, content, `isRead`).
- **`MessageController`**: `GET /api/conversations` (inbox, "otherized" per caller with
  the other user's name/avatar via Feign), `GET /api/conversations/{id}/messages`
  (auth-checked), `POST /api/messages/{recipientId}` (**find-or-create** the
  conversation, store the message, bump `lastMessageAt`). The symmetric find-or-create
  is why replies reuse the same thread instead of creating duplicates.

#### common-service (`:8087`) — reports & saved items
- **Entities**: `Report` (`reason` (`ReportReason`), `status` (`ReportStatus`),
  `targetType` (`TargetType`)); `SavedItem` (a user bookmarking any target).
- **`CommonController`**: `POST /api/reports` (report any entity), and saved-items
  management `POST/DELETE/GET /api/saved`. Uses Feign `exists` checks against the
  owning services to validate report/save targets.

### 4.3 Cross-cutting concerns

- **Authentication flow**: user-service issues a short-lived access JWT + a refresh
  token (hash stored server-side). The frontend stores both, sends
  `Authorization: Bearer <access>` on every call, and the axios interceptor in
  `services/api.ts` transparently calls `/api/auth/refresh-token` on a 401, retries,
  and logs out if refresh fails. The **gateway** validates the access token and adds
  `X-User-Id`; services never re-validate.
- **Service-to-service**: declarative **Feign** clients (`@FeignClient(name=…)`)
  resolved by Eureka. Cross-service reads (names, existence) are best-effort —
  failures degrade gracefully rather than cascading.
- **Persistence**: Spring Data JPA + Hibernate with `ddl-auto=update` (schema is
  derived from entities at startup) and `open-in-view=false` (lazy relations must be
  touched inside `@Transactional` service methods).
- **File uploads**: stored on each service's local disk and served back through
  dedicated static paths via the gateway (`/uploads`, `/coloc-uploads`,
  `/event-uploads`, `/user-uploads`). In Kubernetes these need a persistent volume.

### 4.4 Frontend

React 19 + TypeScript, built with Vite 8 and styled with Tailwind 4. Routing via
`react-router-dom`; HTTP via `axios`.

- **`services/api.ts`** — the configured axios instance: base URL from
  `VITE_API_URL` (defaults to the gateway), a request interceptor that attaches the
  access token, and a response interceptor that auto-refreshes on 401.
  `offerApi.ts` adds offer-specific helpers.
- **`context/AuthContext.tsx`** — global auth state (current user, token), exposed to
  pages via the `useAuth()` hook.
- **`pages/`** — one component per screen: `AuthPage`/`AuthCallbackPage` (login +
  OAuth2 redirect), `HomePage`, `MarketplacePage`/`ItemDetailPage`/`CreateItemPage`,
  `ColocationPage`/`ColocationDetailPage`/`CreateColocationPage`,
  `OffersPage`/`OfferDetailPage`/`CreateOfferPage`/`MyApplicationsPage`,
  `EventsPage`/`EventDetailPage`/`CreateEventPage`, `MessagingPage`, `ProfilePage`.
- **`components/`, `data/`, `assets/`** — shared UI, static data, and images.
- **Build**: `npm run build` runs `tsc -b` (strict; **unused locals fail the build**)
  then `vite build`. The Docker image is a multi-stage build (Node build → Nginx serve)
  that bakes `VITE_API_URL=/api` for same-origin calls behind one ingress host.

---

## 5. Deployment

- **CI/CD** (`Jenkinsfile`): builds each service JAR, builds & pushes Docker images to
  a local registry (`localhost:5000/compuslink-<svc>:<build>`), then applies the
  Kubernetes manifests and pins images.
- **Kubernetes** (`compuslink-microservices/k8s/`): manifests for a **k3s** cluster —
  one Deployment/Service per microservice, a `postgres` StatefulSet-style deployment
  with an init script creating the seven databases, ingress via **Traefik**, and a
  `compuslink-secrets` Secret for DB credentials. Namespace: `compuslink`.
- **Terraform** (`terraform/`): infrastructure provisioning.

> Two known operational gotchas (both documented in the team's notes): services must
> register their pod **IP** with Eureka (`prefer-ip-address=true`), and Hibernate's
> `ddl-auto=update` **cannot add a `NOT NULL` column to an already-populated table** —
> use a manual `ALTER … DEFAULT …` or annotate the field with `@ColumnDefault`.

---

## 6. Port & service reference

| Service | Port | Database | Gateway path prefixes |
|---|---|---|---|
| api-gateway | 8080 | — | (all `/api/**`) |
| eureka-server | 8761 | — | — |
| user-service | 8081 | `compuslink_users` | `/api/auth`, `/api/users`, `/api/me`, `/oauth2` |
| marketplace-service | 8082 | `compuslink_marketplace` | `/api/items`, `/api/marketplace`, `/uploads` |
| colocation-service | 8083 | `compuslink_colocation` | `/api/coloc`, `/coloc-uploads` |
| offer-service | 8084 | `compuslink_offers` | `/api/offers`, `/api/applications` |
| event-service | 8085 | `compuslink_events` | `/api/events`, `/event-uploads` |
| messaging-service | 8086 | `compuslink_messaging` | `/api/messages`, `/api/conversations` |
| common-service | 8087 | `compuslink_common` | `/api/reports`, `/api/saved` |
| postgres | 5432 | (7 databases) | — |
| frontend (dev) | 5173 | — | — |

---

*Generated against the current source. Default credentials and secrets shown here are
local development defaults — set real `DB_PASSWORD`, `JWT_SECRET`, and OAuth2
credentials via environment variables / Kubernetes secrets in any real deployment.*
