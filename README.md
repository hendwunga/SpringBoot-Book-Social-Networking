<div align="center">

# Book Social Network

### A Full-Stack Book Sharing & Social Networking Platform

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-16-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

---

**Book Social Network (BSN)** is a platform where book lovers can share, borrow, and discuss books within a community. Users can register their personal book collections, share them with others, and manage a complete borrow-return lifecycle with owner approval.

[Getting Started](#-getting-started) |
[API Documentation](#-api-documentation) |
[Architecture](#-architecture) |
[Contributing](#-contributing)

</div>

---

## Table of Contents

- [About The Project](#-about-the-project)
- [Key Features](#-key-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [Default Accounts](#-default-accounts)
- [API Documentation](#-api-documentation)
- [Role & Permission System](#-role--permission-system)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Contributing](#-contributing)
- [License](#-license)

---

## About The Project

Book Social Network addresses a simple problem: **sharing books should be easy**. Instead of buying books that sit on shelves, this platform connects readers who want to share their collections.

### How It Works

```
Register -> Verify via Email -> Login -> Share Your Books -> Others Borrow -> Return & Review
```

1. A user **registers** and receives an **activation email** (via MailDev in development)
2. After activation, they **login** and receive JWT tokens
3. They can **create book listings** with details and cover images
4. Other users can **borrow** available (shareable, non-archived) books
5. The borrower **returns** the book, and the owner **approves** the return
6. Borrowers can **leave feedback** and ratings on books they've read

---

## Key Features

### Authentication & Security
- Secure registration with **email-based account activation** (6-digit OTP)
- **JWT-based authentication** with Access Token (24h) and Refresh Token (7d)
- **Role-based access control** (RBAC) with `ADMIN` and `USER` roles
- Stateless session management — no server-side sessions
- Automatic token refresh via HTTP interceptor on the frontend

### Book Management (CRUD)
- Create, read, update, and delete book listings
- Upload **book cover images** (stored locally, up to 50MB)
- Toggle **shareable** status (allow others to borrow)
- Toggle **archived** status (hide from listings)
- **Ownership enforcement** — only the owner can modify their books

### Borrowing System
- Full **borrow-return lifecycle** with owner approval
- Prevents duplicate borrows and self-borrowing
- Tracks borrow status: borrowed -> returned -> return approved
- Separate views for borrowed books and returned books

### Feedback & Ratings
- Rate books on a **0-5 scale** with comments
- **Own-feedback detection** — users see their own feedback flagged
- Paginated feedback listings per book

### Admin Panel
- View all registered users
- **Lock/Unlock** user accounts
- **Enable/Disable** user accounts
- Role-based dashboard routing (ADMIN sees user management, USER sees books)

---

## Architecture

### System Overview

```
┌─────────────────────────────────────────────────────────┐
│                      CLIENT                              │
│                                                          │
│  ┌──────────────┐         ┌──────────────────────────┐  │
│  │   Browser     │ ──────> │  Angular Frontend (:4200) │  │
│  │               │ <────── │  - Login/Register         │  │
│  │               │         │  - Book Management        │  │
│  │               │         │  - Admin Panel            │  │
│  └──────────────┘         └──────────┬───────────────┘  │
│                                       │                  │
└───────────────────────────────────────┼──────────────────┘
                                        │ HTTP/REST
┌───────────────────────────────────────┼──────────────────┐
│                    SERVER             │                   │
│                                       v                  │
│  ┌──────────────────────────────────────────────────┐   │
│  │          Spring Boot API (:8088/api/v1)           │   │
│  │                                                    │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌──────────┐ │   │
│  │  │   Security   │  │  Controllers │  │ Services │ │   │
│  │  │  JWT Filter  │  │  Auth/User/  │  │ Business │ │   │
│  │  │  RBAC        │  │  Book/Feedback│  │ Logic    │ │   │
│  │  └─────────────┘  └─────────────┘  └──────────┘ │   │
│  │                       │                           │   │
│  │  ┌───────────────────┐│  ┌─────────────────────┐ │   │
│  │  │  Email Service    ││  │  File Storage        │ │   │
│  │  │  (Thymeleaf)      ││  │  (Book Covers)       │ │   │
│  │  └───────────────────┘│  └─────────────────────┘ │   │
│  └───────────┬───────────┘───────────┬───────────────┘   │
│              │                       │                    │
└──────────────┼───────────────────────┼────────────────────┘
               │                       │
               v                       v
    ┌──────────────────┐    ┌──────────────────┐
    │   PostgreSQL      │    │    MailDev        │
    │   (:5432)         │    │    SMTP (:1025)   │
    │   book_social_    │    │    Web UI (:1080) │
    │   network         │    │                    │
    └──────────────────┘    └──────────────────┘
```

### Request Flow

```
Client Request
      │
      v
┌─────────────┐    ┌──────────────┐    ┌─────────────────┐
│  JwtFilter   │───>│  Controller   │───>│    Service       │
│  (validate   │    │  (validate    │    │  (business       │
│   token)     │    │   request)    │    │   logic)         │
└─────────────┘    └──────────────┘    └────────┬────────┘
                                                │
                                        ┌───────v────────┐
                                        │   Repository    │
                                        │  (JPA/Hibernate)│
                                        └───────┬────────┘
                                                │
                                        ┌───────v────────┐
                                        │   PostgreSQL    │
                                        └────────────────┘
```

---

## Tech Stack

### Backend

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Language | Java | 17+ | Core runtime |
| Framework | Spring Boot | 3.3.1 | Application framework |
| Security | Spring Security | 6.x | Authentication & authorization |
| JWT | jjwt | 0.11.5 | Token generation & validation |
| ORM | Spring Data JPA / Hibernate | 6.5.2 | Database access |
| Database | PostgreSQL | 14+ | Primary data store |
| Email | Spring Mail + Thymeleaf | - | Activation email templates |
| Validation | Jakarta Bean Validation | - | Request validation |
| API Docs | springdoc-openapi | 2.6.0 | Swagger UI |
| Build | Maven | 3.9+ | Dependency management |
| Code Gen | Lombok | - | Boilerplate reduction |

### Frontend

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Framework | Angular | 16.1.x | SPA framework |
| CSS | Bootstrap | 5.3.3 | UI components |
| Icons | Font Awesome | 6.6.0 | Icon library |
| JWT | @auth0/angular-jwt | 5.2.0 | Client-side JWT handling |
| OTP Input | angular-code-input | 2.0.0 | Activation code input |
| API Gen | ng-openapi-gen | 0.51.0 | OpenAPI client generation |
| Language | TypeScript | 5.1.3 | Type-safe JavaScript |
| Build | Angular CLI | 16.1.4 | Build tooling |

### Infrastructure

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Containerization | Docker Compose | Service orchestration |
| Database | PostgreSQL 14 | Persistent data storage |
| Email Server | MailDev | Development email capture |

---

## Prerequisites

Before running this project, make sure you have the following installed:

| Software | Minimum Version | Check Command | Download |
|----------|----------------|---------------|----------|
| **Java (JDK)** | 17+ | `java -version` | [Oracle](https://www.oracle.com/java/technologies/downloads/) / [Adoptium](https://adoptium.net/) |
| **Node.js** | 18+ | `node -v` | [nodejs.org](https://nodejs.org/) |
| **npm** | 9+ | `npm -v` | Comes with Node.js |
| **Docker** | 20.10+ | `docker -v` | [docker.com](https://www.docker.com/get-started/) |
| **Docker Compose** | 2.0+ | `docker compose version` | Comes with Docker Desktop |
| **Git** | 2.0+ | `git --version` | [git-scm.com](https://git-scm.com/) |

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/hendrowunga/SpringBoot-Book-Social-Networking.git
cd SpringBoot-Book-Social-Networking
```

### 2. Start Infrastructure (Docker)

```bash
docker compose up -d
```

This starts:
- **PostgreSQL** on port `5432`
- **MailDev** Web UI on port `1080`, SMTP on port `1025`

Verify they're running:
```bash
docker compose ps
```

### 3. Configure Environment Variables

```bash
cp .env.example .env
```

Edit `.env` with your own values if needed. See [Environment Variables](#-environment-variables) for details.

### 4. Start the Backend

**Option A: Using the run script (recommended)**
```bash
chmod +x run.sh
./run.sh
```

**Option B: Manual**
```bash
export $(cat .env | grep -v '^#' | xargs)
cd book-network
./mvnw spring-boot:run
```

**Option C: Using IntelliJ IDEA**
1. Import the `book-network` folder as a Maven project
2. Set Environment Variables in Run Configuration (copy from `.env`)
3. Run `BookNetworkApiApplication.java`

The API will start at `http://localhost:8088/api/v1`

### 5. Start the Frontend

```bash
cd book-network-frontend
npm install
ng serve
```

The frontend will start at `http://localhost:4200`

### 6. Verify Everything Works

| Service | URL | Expected |
|---------|-----|----------|
| Backend API | http://localhost:8088/api/v1 | Application starts |
| Swagger UI | http://localhost:8088/api/v1/swagger-ui/index.html | API docs visible |
| Frontend | http://localhost:4200 | Login page visible |
| MailDev | http://localhost:1080 | Email inbox visible |
| pgAdmin / DBeaver | localhost:5432 | Database accessible |

---

## Environment Variables

All configuration is managed through environment variables in the `.env` file. **Never commit `.env` to version control.**

| Variable | Default Value | Description |
|----------|--------------|-------------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/book_social_network` | PostgreSQL JDBC connection URL |
| `DB_USERNAME` | `username` | PostgreSQL database username |
| `DB_PASSWORD` | `password` | PostgreSQL database password |
| `JWT_SECRET_KEY` | `404E63...` | Secret key for signing JWT tokens (Base64 encoded) |
| `JWT_EXPIRATION` | `86400000` | Access token expiration in milliseconds (24 hours) |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Refresh token expiration in milliseconds (7 days) |
| `MAIL_HOST` | `localhost` | SMTP server hostname |
| `MAIL_PORT` | `1025` | SMTP server port |
| `MAIL_USERNAME` | `endos` | SMTP authentication username |
| `MAIL_PASSWORD` | `endos` | SMTP authentication password |
| `ACTIVATION_URL` | `http://localhost:4200/activate-account` | Frontend activation URL sent in emails |

> **Production Note:** For production deployment, generate a new `JWT_SECRET_KEY` using:
> ```bash
> openssl rand -base64 512
> ```

---

## Default Accounts

These accounts are pre-configured for testing:

| Email | Password | Role | Status | Purpose |
|-------|----------|------|--------|---------|
| `john.owner@test.com` | `password123` | **ADMIN + USER** | Active | Admin panel + book management |
| `jane.borrower@test.com` | `password123` | USER | Active | Borrow/return workflow |
| `hendrowunga@test.com` | `password123` | USER | Active | General testing |

### Creating New Admin Users

There is no admin registration endpoint (by design). To make a user admin:

```sql
-- Connect to PostgreSQL
psql -U username -d book_social_network

-- Find the user ID
SELECT id, email FROM _user WHERE email = 'newuser@test.com';

-- Get ADMIN role ID
SELECT id FROM role WHERE name = 'ADMIN';

-- Assign ADMIN role
INSERT INTO user_roles (user_id, role_id) VALUES (<user_id>, <role_id>);
```

---

## API Documentation

All endpoints are prefixed with `/api/v1`. Interactive documentation is available at:

> **Swagger UI:** http://localhost:8088/api/v1/swagger-ui/index.html

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|:------------:|
| `POST` | `/auth/register` | Register a new user (sends activation email) | No |
| `POST` | `/auth/authenticate` | Login and receive JWT tokens | No |
| `GET` | `/auth/activate-account?token={code}` | Activate account with 6-digit code | No |
| `POST` | `/auth/refresh-token` | Get new access token using refresh token | Refresh Token |

<details>
<summary>Request/Response Examples</summary>

**POST /auth/register**
```json
// Request
{
  "firstname": "John",
  "lastname": "Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}

// Response: 202 Accepted (empty body)
```

**POST /auth/authenticate**
```json
// Request
{
  "email": "john@example.com",
  "password": "securePassword123"
}

// Response: 200 OK
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
  "refreshToken": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "roles": ["USER"]
}
```

**POST /auth/refresh-token**
```bash
# Header: Authorization: Bearer <refresh_token>

// Response: 200 OK
{
  "accessToken": "eyJhbGciOiJIUzM4NCJ9...",
  "refreshToken": "new-refresh-token-uuid",
  "roles": ["USER"]
}
```
</details>

### Book Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|:------------:|
| `POST` | `/books` | Create a new book listing | Yes |
| `GET` | `/books` | Get all shareable books (paginated) | Yes |
| `GET` | `/books/{book-id}` | Get book details by ID | Yes |
| `GET` | `/books/owner` | Get current user's books (paginated) | Yes |
| `GET` | `/books/borrowed` | Get books borrowed by current user | Yes |
| `GET` | `/books/returned` | Get books returned by current user | Yes |
| `PATCH` | `/books/shareable/{book-id}` | Toggle shareable status | Yes (Owner) |
| `PATCH` | `/books/archived/{book-id}` | Toggle archived status | Yes (Owner) |
| `POST` | `/books/borrow/{book-id}` | Borrow a book | Yes |
| `PATCH` | `/books/borrow/return/{book-id}` | Return a borrowed book | Yes (Borrower) |
| `PATCH` | `/books/borrow/return/approve/{book-id}` | Approve book return | Yes (Owner) |
| `POST` | `/books/cover/{book-id}` | Upload book cover image | Yes (Owner) |

<details>
<summary>Request/Response Examples</summary>

**POST /books**
```json
// Request
{
  "title": "Effective Java",
  "authorName": "Joshua Bloch",
  "isbn": "978-0134685991",
  "synopsis": "A must-read for every Java programmer.",
  "shareable": true
}

// Response: 200 OK
42
```

**GET /books?page=0&size=5**
```json
// Response: 200 OK
{
  "content": [
    {
      "id": 1,
      "title": "The Great Gatsby",
      "authorName": "F. Scott Fitzgerald",
      "isbn": "978-0743273565",
      "synopsis": "A story of the mysteriously wealthy Jay Gatsby...",
      "owner": "John Doe",
      "cover": "base64-encoded-image...",
      "rate": 4.5,
      "archived": false,
      "shareable": true
    }
  ],
  "number": 0,
  "size": 5,
  "totalElements": 12,
  "totalPages": 3,
  "first": true,
  "last": false
}
```

**POST /books/cover/{book-id}**
```bash
# Content-Type: multipart/form-data
# Body: file=<binary image data>

// Response: 202 Accepted
```
</details>

### Feedback

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|:------------:|
| `POST` | `/feedbacks` | Submit feedback for a book | Yes |
| `GET` | `/feedbacks/book/{book-id}` | Get all feedback for a book (paginated) | Yes |

<details>
<summary>Request/Response Examples</summary>

**POST /feedbacks**
```json
// Request
{
  "note": 4.5,
  "comment": "Amazing book! Great story about the American dream.",
  "bookId": 1
}

// Response: 200 OK
1
```

**GET /feedbacks/book/1?page=0&size=5**
```json
// Response: 200 OK
{
  "content": [
    {
      "note": 4.5,
      "comment": "Amazing book! Great story about the American dream.",
      "ownFeedback": true
    }
  ],
  "number": 0,
  "size": 5,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```
</details>

### User Management (Admin Only)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|:------------:|
| `GET` | `/users` | Get all users (paginated) | ADMIN |
| `GET` | `/users/{user-id}` | Get user details by ID | ADMIN |
| `PATCH` | `/users/{user-id}/lock` | Toggle user lock status | ADMIN |
| `PATCH` | `/users/{user-id}/enable` | Toggle user enabled status | ADMIN |
| `GET` | `/users/profile` | Get own profile | Any User |

### API Testing Workflow

1. **Register** a new account via `POST /auth/register`
2. **Check MailDev** at http://localhost:1080 for the activation email
3. **Activate** via `GET /auth/activate-account?token={6-digit-code}`
4. **Login** via `POST /auth/authenticate` to get JWT tokens
5. **Use the Access Token** as `Authorization: Bearer {accessToken}` header
6. **Test protected endpoints** via Swagger UI or your preferred HTTP client

---

## Role & Permission System

### Role Definitions

| Role | Description | How to Assign |
|------|-------------|---------------|
| `USER` | Default role for all registered users | Automatically assigned on registration |
| `ADMIN` | Platform administrator | Manually assigned via database |

### Permission Matrix

| Action | USER | ADMIN |
|--------|:----:|:-----:|
| Register | Yes | - |
| Login | Yes | Yes |
| Create books | Yes (own) | Yes (own) |
| Edit books | Yes (own) | Yes (own) |
| Delete/archive books | Yes (own) | Yes (own) |
| Toggle shareable | Yes (own) | Yes (own) |
| Upload book cover | Yes (own) | Yes (own) |
| Browse all books | Yes | Yes |
| Borrow books | Yes | Yes |
| Return books | Yes | Yes |
| Approve returns | Yes (owner) | Yes (owner) |
| Give feedback | Yes | Yes |
| **View all users** | **No** | **Yes** |
| **Lock/Unlock users** | **No** | **Yes** |
| **Enable/Disable users** | **No** | **Yes** |

### Ownership Enforcement

The system enforces **strict ownership** rules:
- Users can only **edit, archive, toggle shareable, and upload covers** for books they own
- Users can only **approve returns** for books they own
- Users **cannot borrow their own books**
- **ADMIN cannot modify other users' books** — admin manages users, not content

This design follows the principle: **"Admin is a platform manager, not a content censor."**

---

## Project Structure

```
SpringBoot-Book-Social-Networking/
├── .env                          # Environment variables (DO NOT COMMIT)
├── .env.example                  # Environment template
├── .gitignore
├── docker-compose.yml            # PostgreSQL + MailDev
├── run.sh                        # Dev runner script
├── README.md
│
├── book-network/                 # ===== BACKEND (Spring Boot) =====
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   └── src/main/
│       ├── java/com/endos/book/
│       │   ├── BookNetworkApiApplication.java   # Entry point + role init
│       │   │
│       │   ├── auth/               # Authentication module
│       │   │   ├── AuthenticationController.java
│       │   │   ├── AuthenticationService.java
│       │   │   ├── AuthenticationRequest.java
│       │   │   ├── AuthenticationResponse.java
│       │   │   └── RegistrationRequest.java
│       │   │
│       │   ├── book/               # Book management module
│       │   │   ├── Book.java
│       │   │   ├── BookController.java
│       │   │   ├── BookMapper.java
│       │   │   ├── BookRepository.java
│       │   │   ├── BookRequest.java
│       │   │   ├── BookResponse.java
│       │   │   ├── BookService.java
│       │   │   ├── BookSpecification.java
│       │   │   └── BorrowedBookResponse.java
│       │   │
│       │   ├── feedback/           # Feedback module
│       │   │   ├── Feedback.java
│       │   │   ├── FeedbackController.java
│       │   │   ├── FeedbackService.java
│       │   │   ├── FeedbackRequest.java
│       │   │   └── FeedbackResponse.java
│       │   │
│       │   ├── user/               # User management module
│       │   │   ├── User.java
│       │   │   ├── UserController.java
│       │   │   ├── UserService.java
│       │   │   ├── UserResponse.java
│       │   │   ├── UserRepository.java
│       │   │   ├── Token.java
│       │   │   └── TokenRepository.java
│       │   │
│       │   ├── role/               # Role management
│       │   │   ├── Role.java
│       │   │   └── RoleRepository.java
│       │   │
│       │   ├── security/           # Security configuration
│       │   │   ├── SecurityConfig.java
│       │   │   ├── JwtFilter.java
│       │   │   ├── JwtService.java
│       │   │   └── UserDetailsServiceImpl.java
│       │   │
│       │   ├── email/              # Email service
│       │   │   ├── EmailService.java
│       │   │   └── EmailTemplateName.java
│       │   │
│       │   ├── file/               # File storage
│       │   │   ├── FileStorageService.java
│       │   │   └── FileUtils.java
│       │   │
│       │   ├── handler/            # Exception handling
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   ├── BusinessErrorCodes.java
│       │   │   └── ExceptionResponse.java
│       │   │
│       │   ├── history/            # Borrow history
│       │   │   ├── BookTransactionHistory.java
│       │   │   └── BookTransactionHistoryRepository.java
│       │   │
│       │   ├── common/             # Shared components
│       │   │   ├── PageResponse.java
│       │   │   └── BaseEntity.java
│       │   │
│       │   ├── config/             # App configuration
│       │   │   ├── BeansConfig.java
│       │   │   ├── ApplicationAuditAware.java
│       │   │   └── OpenApiConfig.java
│       │   │
│       │   └── exception/
│       │       └── OperationNotPermittedException.java
│       │
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           └── templates/
│               └── activate_account.html
│
└── book-network-frontend/         # ===== FRONTEND (Angular) =====
    ├── package.json
    ├── angular.json
    └── src/app/
        ├── app.module.ts
        ├── app-routing.module.ts
        │
        ├── pages/                  # Public pages
        │   ├── login/
        │   ├── register/
        │   └── activate-account/
        │
        ├── modules/book/           # Book module (lazy-loaded)
        │   ├── book.module.ts
        │   ├── book-routing.module.ts
        │   │
        │   ├── pages/
        │   │   ├── main/
        │   │   ├── book-list/
        │   │   ├── book-details/
        │   │   ├── my-books/
        │   │   ├── manage-book/
        │   │   ├── borrowed-book-list/
        │   │   ├── return-books/
        │   │   └── manage-users/       # Admin panel
        │   │
        │   └── components/
        │       ├── menu/               # Role-aware navbar
        │       ├── book-card/
        │       └── rating/
        │
        └── services/
            ├── token/token.service.ts      # JWT + role management
            ├── interceptor/http-token.interceptor.ts
            ├── guard/auth.guard.ts
            ├── models/                     # TypeScript interfaces
            ├── services/                   # API services
            │   ├── authentication.service.ts
            │   ├── book.service.ts
            │   ├── feedback.service.ts
            │   └── user.service.ts
            └── fn/                         # Generated API functions
```

---

## Database Schema

### Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│     role     │       │   user_roles     │       │    _user     │
├──────────────┤       ├──────────────────┤       ├──────────────┤
│ id (PK)      │◄──────│ role_id (FK)     │       │ id (PK)      │
│ name (UNIQUE)│       │ user_id (FK)     │──────►│ email (UNIQUE)│
│ created_date │       └──────────────────┘       │ firstname    │
│ last_mod_date│                                  │ lastname     │
└──────────────┘                                  │ password     │
                                                  │ enabled      │
                                                  │ account_locked│
                                                  │ created_date │
                                                  └──────┬───────┘
                                                         │
                        ┌────────────────────────────────┤
                        │                                │
              ┌─────────v──────────┐          ┌──────────v──────────┐
              │       book         │          │   book_transaction_  │
              ├────────────────────┤          │      history         │
              │ id (PK)            │◄─────────├─────────────────────┤
              │ title              │          │ id (PK)             │
              │ author_name        │          │ book_id (FK)        │
              │ isbn               │          │ user_id (FK)        │
              │ synopsis           │          │ returned            │
              │ owner_id (FK)      │──┐       │ return_approved     │
              │ book_cover         │  │       │ created_date        │
              │ archived           │  │       └─────────────────────┘
              │ shareable          │  │
              │ rate               │  │       ┌─────────────────────┐
              │ created_date       │  │       │     feedback        │
              └────────────────────┘  │       ├─────────────────────┤
                                      │       │ id (PK)             │
                                      └───────│ book_id (FK)        │
                                              │ user_id (FK)        │
                                              │ note (0-5)          │
                                              │ comment             │
                                              │ created_date        │
                                              └─────────────────────┘
```

### Table Summary

| Table | Records | Purpose |
|-------|---------|---------|
| `_user` | User accounts | Stores all registered users |
| `role` | USER, ADMIN | Role definitions |
| `user_roles` | M:N junction | Maps users to their roles |
| `token` | JWT tokens | Stores access & refresh tokens |
| `book` | Book listings | All books created by users |
| `book_transaction_history` | Borrow records | Tracks borrow/return lifecycle |
| `feedback` | Ratings & comments | User feedback on books |

---

## Contributing

Contributions are welcome! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'feat: add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Commit Convention

This project follows [Conventional Commits](https://www.conventionalcommits.org/):

| Prefix | Description |
|--------|-------------|
| `feat:` | New feature |
| `fix:` | Bug fix |
| `docs:` | Documentation changes |
| `style:` | Code style changes (formatting, etc.) |
| `refactor:` | Code refactoring |
| `test:` | Adding or updating tests |
| `chore:` | Build process or tooling changes |

---

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

<div align="center">

**Built with Spring Boot + Angular + PostgreSQL**

*Book Social Network - Share knowledge, one book at a time.*

</div>
