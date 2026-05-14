# Fly Away Travel — API REST (CS 2031 DBP, Lab 07)

API REST en **Spring Boot 3 + Java 17** para reservar vuelos. Implementa **JWT** para autenticación y sigue **arquitectura en N capas** con separación estricta de responsabilidades.

---

## 📁 Estructura de carpetas (N-capas)

```
src/main/java/com/example/flyawaytravel/
├── FlyAwayTravelApplication.java   ← entry point + bean ModelMapper
├── auth/                           ← seguridad / JWT
│   ├── AuthController.java
│   ├── AuthService.java            ← UserDetailsService + login()
│   ├── JwtService.java             ← generar, validar, extraer claims
│   └── JwtAuthFilter.java          ← OncePerRequestFilter
├── config/
│   └── SecurityConfig.java         ← SecurityFilterChain, BCrypt, stateless
├── controller/                     ← capa REST (solo orquestación)
│   ├── UserController.java
│   ├── FlightController.java
│   ├── BookingAliasController.java ← alias GET /flight/book/{id}
│   └── CleanupController.java
├── service/                        ← capa de negocio
│   ├── UserService.java
│   ├── FlightService.java
│   ├── BookingService.java
│   └── CleanupService.java
├── repository/                     ← capa de persistencia (JPA)
│   ├── UserRepository.java
│   ├── FlightRepository.java
│   └── BookingRepository.java
├── domain/                         ← entidades JPA
│   ├── User.java                   ← implements UserDetails
│   ├── Flight.java
│   └── Booking.java
├── dto/                            ← contratos de entrada/salida
│   ├── RequestAuthDTO.java
│   ├── RequestUserDTO.java
│   ├── RequestFlightDTO.java
│   ├── RequestBookingDTO.java
│   ├── ResponseAuthDTO.java
│   ├── ResponseUserDTO.java
│   ├── ResponseFlightDTO.java
│   └── ResponseBookingDTO.java
└── exception/                      ← excepciones + handler global
    ├── ResourceNotFoundException.java
    ├── ConflictException.java
    ├── BusinessException.java
    ├── UnauthorizedException.java
    └── GlobalExceptionHandler.java
```

**Flujo de una request:** `Controller → Service → Repository → BD`. Los Controllers nunca tocan repositorios directamente; los DTOs nunca llegan a la capa de persistencia (se mapean con `ModelMapper`); las excepciones de negocio se convierten en respuestas HTTP en `GlobalExceptionHandler`.

---

## 🚀 Cómo correrlo

### 1. Levantar PostgreSQL con Docker

```bash
docker compose up -d
```

### 2. Correr la app desde IntelliJ

Abrir el proyecto → esperar a que Maven descargue dependencias → ejecutar `FlyAwayTravelApplication.main()`.

O por terminal:

```bash
./mvnw spring-boot:run        # Linux / Mac
mvnw.cmd spring-boot:run      # Windows
```

La API queda en `http://localhost:8080`.

### 3. Correr los tests del laboratorio

```bash
java -jar week07-tester.jar test -u http://localhost:8080
```

---

## 🔌 Endpoints

| Método | Ruta                  | Auth | Descripción                       |
|--------|-----------------------|------|-----------------------------------|
| POST   | `/users/register`     | ❌   | Registrar usuario                 |
| POST   | `/auth/login`         | ❌   | Login, retorna `{ "token": "..." }` |
| POST   | `/flights/create`     | ❌   | Crear vuelo                       |
| GET    | `/flights/search`     | ✅   | Buscar vuelos (filtros opcionales) |
| GET    | `/flights/{id}`       | ✅   | Obtener vuelo por id              |
| POST   | `/flights/book`       | ✅   | Reservar vuelo                    |
| GET    | `/flights/book/{id}`  | ✅   | Ver reserva                       |
| GET    | `/flight/book/{id}`   | ✅   | Alias singular (lo pide el PDF)   |
| GET    | `/users/{id}`         | ✅   | Obtener usuario por id            |
| DELETE | `/cleanup`            | ❌   | Limpiar la BD (para los tests)    |

### Parámetros de `/flights/search`

- `flightNumber` — match parcial, case-insensitive
- `airLineName`  — match parcial, case-insensitive
- `from`         — ISO 8601 (ej: `2026-05-20T00:00:00Z`)
- `to`           — ISO 8601

### Header de autenticación

```
Authorization: Bearer <token>
```

---

## ✅ Misiones cumplidas

### MUST HAVE
- [x] Crear vuelo con todas las constraints (regex, fechas, asientos, único)
- [x] Registro de usuarios con validación (email, mayúsculas, password)
- [x] Autenticación JWT (`/auth/login`) con validación de email y contraseña
- [x] Búsqueda de vuelos por número y aerolínea (parcial)
- [x] Reservar vuelo (con flightId, autoasigna cliente, no sobrevende)
- [x] Endpoints `GET /{entity}/{id}` para User, Flight, Booking
- [x] `DELETE /cleanup`

### NICE TO HAVE
- [x] Registro de usuario devuelve solo el `id`
- [x] Búsqueda por rango de fechas (`from`/`to`)
- [x] No se pueden reservar vuelos pasados o en tránsito
- [x] Conflicto de horario entre reservas del mismo usuario
- [x] Archivo `flight_booking_email_${booking_id}.txt` con fechas ISO 8601

---

## 🔐 Notas de seguridad

- Contraseñas hasheadas con **BCrypt**.
- JWT firmado con HS256 (`jjwt 0.12.6`). La secret va por variable de entorno `JWT_SECRET` (Base64, mínimo 256 bits).
- Sesiones **stateless** — no hay cookies de sesión.
- `@PreAuthorize` por endpoint + filtro JWT que popula el `SecurityContext`.

## 🧪 Probar manualmente con curl

```bash
# Registrar usuario
curl -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"juan@mail.com","email":"juan@mail.com","firstName":"Juan","lastName":"Perez","password":"abc12345"}'

# Login
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"juan@mail.com","password":"abc12345"}' | jq -r .token)

# Crear vuelo
curl -X POST http://localhost:8080/flights/create \
  -H "Content-Type: application/json" \
  -d '{"airLineName":"LATAM","flightNumber":"LA123","estDepartureTime":"2026-12-01T10:00:00.000Z","estArrivalTime":"2026-12-01T14:00:00.000Z","availableSeats":100}'

# Buscar vuelos
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/flights/search?airLineName=LATAM"

# Reservar
curl -X POST http://localhost:8080/flights/book \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"flightId":1}'
```
