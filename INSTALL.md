# 🎟 MetalEv-CL - Full-Stack Integration
## 🛠 Stack Tecnológico
* **Backend:** Java 21, Spring Boot 3, Spring Data JPA, Hibernate, OpenAPI/Swagger.
* **Frontend:** TypeScript Vanilla, Vite, Native ESM, SCSS, HTML5/CSS3 Semántico.
* **Infraestructura:** Docker Compose, PostgreSQL 16 Alpine.
* **Calidad y Testing:** JUnit 5, Mockito, JaCoCo, TDD & Clean Architecture.
---
## 🔗 Repositorios de Referencia
* Core de Dominio / Hito 1: https://github.com/mjverap/metalev-cl
* Backend Spring Boot / Hito 4: https://github.com/mjverap/metalev-cl-api
* Frontend Vite + TS / Hito 2: https://github.com/mjverap/metalev-cl-frontend
---
## 🚀 Guía de Puesta en Marcha Local
### 1. Clonar repositorios de _backend_ y _frontend_

```bash
git clone https://github.com/mjverap/metalev-cl-api
git clone https://github.com/mjverap/metalev-cl-frontend
```

### 2. Levantar la Base de Datos Relacional

```bash
cd metalev-cl-api
docker compose up -d
```

### 3. Ejecutar Pruebas Automatizadas

```bash
./mvnw clean test
```

### 4. Iniciar el Microservicio Backend

```bash
./mvnw spring-boot:run
```

- API REST: http://localhost:8080/api/v1/recitals
- Swagger UI (Perfil Dev): http://localhost:8080/swagger-ui.html

### 5. Iniciar la Interfaz Web Frontend

```bash
cd ../metalev-cl-frontend
pnpm install
pnpm run dev
```
- App Web: http://localhost:5173