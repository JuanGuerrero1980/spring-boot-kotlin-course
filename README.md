# Spring Boot + Kotlin — PayFlow API

Curso práctico de desarrollo backend con **Spring Boot** y **Kotlin**, construyendo paso a paso **PayFlow API**: una plataforma de pagos y recargas diseñada como proyecto real de portfolio.

> **Objetivo:** Dominar Spring Boot con Kotlin a nivel profesional y tener un repositorio que puedas mostrar en procesos de selección.

---

## Tecnologías

- **Kotlin**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **Spring Security + JWT**
- **PostgreSQL**
- **Gradle (Kotlin DSL)**
- **JUnit, Mockito, MockMvc, Testcontainers**
- **Docker y Docker Compose**
- **GitHub Actions**

---

## Estructura del repositorio

```text
spring-boot-kotlin-course/
│
├── course/              # Material teórico del curso, clase por clase
├── docs/                # Documentación viva de la API
├── exercises/           # Ejercicios complementarios
└── payflow-api/         # Proyecto Spring Boot que construimos
```

---

## Cómo ejecutar el proyecto

```bash
cd payflow-api
./gradlew bootRun
```

La aplicación se levanta por defecto en:

```text
http://localhost:8080
```

Endpoints de prueba:

```text
GET /api/hello
GET /api/status
GET /api/headers
```

---

## Cómo correr los tests

```bash
cd payflow-api
./gradlew test
```

---

## Índice del curso

Ver [`course/00-roadmap/INDEX.md`](course/00-roadmap/INDEX.md).

---

## Roadmap

Ver [`course/00-roadmap/ROADMAP.md`](course/00-roadmap/ROADMAP.md).

---

## Sobre PayFlow API

PayFlow es una API REST ficticia de pagos y recargas. A lo largo del curso vamos agregando:

- autenticación y autorización con JWT;
- usuarios, cuentas, pagos y recargas;
- persistencia en PostgreSQL;
- manejo global de excepciones;
- tests unitarios e integración;
- documentación OpenAPI;
- Docker y CI/CD.

Cada clase incrementa el proyecto de forma controlada.
