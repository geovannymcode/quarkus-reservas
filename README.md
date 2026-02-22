# Reservas Profesionales — Evaluación Final Quarkus 2026

Sistema REST para el control de reservas y disponibilidad de profesionales en un centro de servicios (psicología, mentorías, asesorías, tutorías).

---

## Decisiones Técnicas

### Arquitectura: DDD Ligero (Domain / Application / Infrastructure)

```
src/main/java/com/mitocode/reservas/
├── domain/
│   ├── model/          → Entidades JPA (Profesional, Cliente, HorarioDisponible, Reserva)
│   ├── enums/          → EstadoReserva (CREADA, CANCELADA, COMPLETADA)
│   └── exception/      → Jerarquía de excepciones de negocio con código HTTP incluido
├── application/
│   ├── dto/
│   │   ├── request/    → Java Records con @Valid (ProfesionalRequest, ClienteRequest, ...)
│   │   └── response/   → Java Records inmutables (ProfesionalResponse, ReservaResponse, ...)
│   └── service/        → Lógica de negocio, Mutiny Uni<T>, reglas de dominio
└── infrastructure/
    ├── repository/     → PanacheRepositoryBase<Entity, UUID> — consultas JPQL reactivas
    ├── resource/       → JAX-RS + OpenAPI — endpoints REST
    ├── mapper/         → Conversión Entity ↔ DTO (sin MapStruct, mappers explícitos)
    └── handler/        → ExceptionMapper para BusinessException, ConstraintViolationException
```

### Stack Tecnológico

| Componente | Versión | Rol |
|---|---|---|
| Quarkus | 3.17.4 | Framework principal |
| Hibernate Reactive + Panache | (via BOM) | ORM reactivo |
| Vert.x PG Client | (via BOM) | Driver reactivo PostgreSQL |
| Mutiny | (via BOM) | Programación reactiva (Uni/Multi) |
| SmallRye OpenAPI | (via BOM) | Documentación OpenAPI 3 / Swagger UI |
| SmallRye Fault Tolerance | (via BOM) | Resiliencia (@Retry, @Timeout, @Fallback) |
| Flyway | (via BOM) | Migraciones de base de datos |
| PostgreSQL | 16 | Base de datos |
| Lombok | 1.18.36 | Reducción de boilerplate en entidades JPA |
| Java | 21 | JVM target (records, text blocks) |

### Patrones y Convenciones

- **Constructor injection** con campos `final` (según reglas globales)
- **Java Records** para todos los DTOs (inmutabilidad, concisión)
- **Lombok** específico: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` (NO `@Data`)
- **Streams API** para consultas funcionales en memoria (ranking, agrupación por fecha)
- **Optional** para retornos potencialmente nulos en repositorios
- **Excepciones de dominio** con jerarquía (`BusinessException` → especializadas)
- **SRP** estricto: cada clase tiene una única responsabilidad

### Programación Reactiva con Mutiny

Todos los endpoints y operaciones con BD usan `Uni<T>`:
- `@WithSession` en métodos de lectura
- `@WithTransaction` en métodos de escritura (garantiza sesión reactiva y transacción)
- `Uni.combine().all().unis(...).asTuple()` para operaciones paralelas

### Fault Tolerance (SmallRye)

El método `listarProfesionalesPorReservasActivas()` en `ReservaService` está protegido con:
- `@Retry(maxRetries = 3, delay = 200ms)` — reintenta ante fallos transitorios
- `@Timeout(value = 10s)` — timeout máximo
- `@Fallback(fallbackMethod = "listarProfesionalesVacio")` — retorna lista vacía si falla

### Programación Funcional

**Ranking de profesionales** (en memoria, con Streams):
```java
reservas.stream()
    .collect(groupingBy(Reserva::getProfesional, counting()))
    .entrySet().stream()
    .sorted(Map.Entry.<Profesional, Long>comparingByValue().reversed())
    .map(e -> new ProfesionalConReservasResponse(toResponse(e.getKey()), e.getValue()))
    .collect(toList())
```

**Reservas agrupadas por fecha** (Map<LocalDate, List<ReservaResponse>>):
```java
reservas.stream()
    .map(reservaMapper::toResponse)
    .collect(groupingBy(ReservaResponse::fecha, TreeMap::new, toList()))
```

---

## Instrucciones de Ejecución

### Prerrequisitos

- Java 21+
- Maven 3.9+ (o usar `./mvnw`)
- Docker (para Dev Services en modo dev/test, o para docker-compose)

### Modo Desarrollo (Dev Services — PostgreSQL automático)

```bash
cd reservas-profesionales
./mvnw quarkus:dev
```

Quarkus Dev Services levanta automáticamente un contenedor PostgreSQL vía Docker.
La aplicación queda disponible en `http://localhost:8080`.

### Modo Producción con Docker Compose

```bash
# 1. Compilar
./mvnw package -DskipTests

# 2. Levantar PostgreSQL + App
docker-compose up -d

# 3. Ver logs
docker-compose logs -f app
```

### Compilación y ejecución manual

```bash
# Compilar (genera target/quarkus-app/)
./mvnw package -DskipTests

# Ejecutar (requiere PostgreSQL corriendo en localhost:5432)
java -jar target/quarkus-app/quarkus-run.jar \
  -Dquarkus.datasource.username=postgres \
  -Dquarkus.datasource.password=postgres
```

### Imagen Docker JVM

```bash
./mvnw package -DskipTests
docker build -f Dockerfile.jvm -t reservas-profesionales:jvm .
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  reservas-profesionales:jvm
```

### Imagen Docker Nativa (GraalVM)

```bash
# Compila con GraalVM en contenedor (no necesita GraalVM local)
./mvnw package -Pnative -Dquarkus.native.container-build=true -DskipTests

docker build -f Dockerfile.native -t reservas-profesionales:native .
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  reservas-profesionales:native
```

### Ejecutar Tests

```bash
# Tests con @QuarkusTest (Dev Services levanta PostgreSQL automáticamente)
./mvnw test
```

---

## Endpoints de la API

### Swagger UI
`http://localhost:8080/swagger-ui`

### OpenAPI spec
`http://localhost:8080/q/openapi`

### Resumen de endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/profesionales` | Listar todos los profesionales |
| GET | `/api/profesionales/{id}` | Buscar profesional por ID |
| POST | `/api/profesionales` | Crear profesional |
| PUT | `/api/profesionales/{id}` | Actualizar profesional |
| DELETE | `/api/profesionales/{id}` | Eliminar profesional |
| GET | `/api/profesionales/ranking/por-reservas-activas` | Ranking funcional por reservas activas |
| GET | `/api/clientes` | Listar todos los clientes |
| GET | `/api/clientes/{id}` | Buscar cliente por ID |
| POST | `/api/clientes` | Crear cliente (email único) |
| PUT | `/api/clientes/{id}` | Actualizar cliente |
| DELETE | `/api/clientes/{id}` | Eliminar cliente |
| GET | `/api/horarios` | Listar horarios disponibles |
| GET | `/api/horarios/profesional/{id}` | Horarios de un profesional |
| POST | `/api/horarios` | Registrar horario (anti-solapamiento) |
| DELETE | `/api/horarios/{id}` | Eliminar horario |
| GET | `/api/reservas` | Listar todas las reservas |
| GET | `/api/reservas/{id}` | Buscar reserva por ID |
| POST | `/api/reservas` | Crear reserva (todas las reglas de negocio) |
| PATCH | `/api/reservas/{id}/cancelar` | Cancelar reserva (libera disponibilidad) |
| GET | `/api/reservas/agrupadas/por-fecha` | Reservas agrupadas por fecha (funcional) |

---

## Variables de Entorno

| Variable | Default | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host del servidor PostgreSQL |
| `DB_PORT` | `5432` | Puerto de PostgreSQL |
| `DB_NAME` | `reservas_db` | Nombre de la base de datos |
| `DB_USERNAME` | `postgres` | Usuario de la base de datos |
| `DB_PASSWORD` | `postgres` | Contraseña de la base de datos |

---

## Reglas de Negocio Implementadas

1. **Anti-solapamiento de horarios**: Al registrar un `HorarioDisponible`, se verifica que no exista otro del mismo profesional en la misma fecha con rango de tiempo superpuesto.

2. **Validación de disponibilidad para reservas**: Una reserva solo se crea si existe un `HorarioDisponible` con `estado=true` que cubra completamente el intervalo solicitado.

3. **Anti-solapamiento de reservas activas**: No se permiten reservas en estado `CREADA` que se superpongan en tiempo para el mismo profesional.

4. **Estados activos obligatorios**: El cliente y el profesional deben tener `estadoActivo=true` para poder crear una reserva.

5. **Cancelación con liberación**: Al cancelar una reserva (`CANCELADA`), el horario que fue bloqueado vuelve a `estado=true` (disponible).
