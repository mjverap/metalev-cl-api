# 🤘 Metal Events CL API (`metalev-cl-api`)

**Gestor de recitales de metal en Chile** - Proyecto Java orientado a arquitectura limpia, DDD táctico y validación de negocio en el dominio. La aplicación expone endpoints versionados, persiste datos en PostgreSQL y publica su contrato mediante OpenAPI.

---

## 🎯 Descripción del microservicio

El servicio centraliza operaciones de catálogo y administración de eventos:

- Gestión de recitales (listado, detalle, creación, actualización parcial y eliminación).
- Gestión de venues (listado con filtros, creación, actualización de dirección y eliminación).
- Endpoint de estado (`/healthcheck`) para monitoreo básico.

Su diseño separa reglas de negocio, casos de uso e infraestructura web/persistencia, facilitando mantenimiento y evolución del código.

---

## 🛠️ Stack tecnológico

- **Java 21**
- **Spring Boot 3.5.x**
- **Spring Web**
- **Spring Data JPA**
- **PostgreSQL**
- **Docker / Docker Compose**
- **OpenAPI (SpringDoc)**

---

## ✅ Requisitos previos

- Java JDK 21 o superior
- Docker + Docker Compose
- Maven Wrapper (`./mvnw`)

---

## 🚀 Levantar la base de datos

Desde la raíz del proyecto:

```bash
docker compose up -d
```

Esto inicia PostgreSQL usando la configuración de `compose.yml`.

---

## ▶️ Ejecutar la aplicación en modo desarrollo

Desde la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

URL base local: `http://localhost:8080`

---

## 📚 Documentación y pruebas de contratos

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

Estas rutas permiten inspeccionar el contrato REST y probar requests/responses del servicio.

---

## 🌐 Matriz resumida de endpoints

### Recitals (`/api/v1/recitals`)

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/v1/recitals` | Lista recitales con filtros opcionales |
| `GET` | `/api/v1/recitals/{id}` | Obtiene detalle por ID |
| `POST` | `/api/v1/recitals` | Crea un recital |
| `PATCH` | `/api/v1/recitals/{id}` | Actualiza parcialmente un recital |
| `DELETE` | `/api/v1/recitals/{id}` | Elimina un recital |

### Venues (`/api/v1/venues`)

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/v1/venues` | Lista venues con filtros opcionales (`region`, `city`) |
| `POST` | `/api/v1/venues` | Crea un venue |
| `PATCH` | `/api/v1/venues/{id}/address` | Actualiza dirección del venue |
| `DELETE` | `/api/v1/venues/{id}` | Elimina un venue |

### Health

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/healthcheck` | Estado del servicio (`status: UP`) |

---

## ⚙️ Variables de entorno

Configuración local mínima (archivo `.env`):

```env
SERVER_PORT=8080
DB_HOST=localhost
DB_PORT=5432
DB_NAME=metalev_db
DB_USER=user_db
DB_PASSWORD=pass_db
```

---

## 🧪 Pruebas

```bash
./mvnw test
```

Para ejecutar pruebas desde limpio:

```bash
./mvnw clean test
```
