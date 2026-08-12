# AGENTS.md — Spring Boot + Kotlin Course

Este archivo contiene las convenciones que deben respetarse al editar este repositorio.

## Propósito del repositorio

Este es un curso de Spring Boot con Kotlin que incluye:

- Material didáctico clase por clase (`course/`).
- Documentación viva del proyecto (`docs/`).
- Ejercicios para el alumno (`exercises/`).
- Proyecto Spring Boot real (`payflow-api/`).

Todo cambio debe mantener la coherencia entre el material teórico, los ejercicios y el código del proyecto.

---

## Convenciones de contenido

### Clases (`course/`)

- Cada clase sigue el formato definido en las plantillas.
- Incluir: objetivos, explicación, ejercicios, preguntas de entrevista, resumen y checklist.
- Los **ejercicios deben publicarse sin respuestas**. Las respuestas se resuelven por el alumno.
- Si se agrega una solución, debe ir en un archivo separado bajo `exercises/solutions/`.
- Usar ejemplos de código en Kotlin.
- Preferir claridad sobre complejidad.

### Proyecto (`payflow-api/`)

- Paquete raíz: `com.payflow.api`.
- Usar **constructor injection** en controllers, services y components.
- Preferir `val` sobre `var`.
- Usar `data class` para DTOs.
- Usar `BigDecimal` para dinero.
- No exponer entidades JPA directamente como respuestas HTTP.
- Mantener separación de responsabilidades: Controller → Service → Repository.
- Cada endpoint debe tener su propio DTO de request/response cuando corresponda.

### Commits

```text
lesson(N): descripción breve
```

Ejemplos:

```text
lesson(3): primer proyecto Spring Boot
lesson(5): implementar RechargeController con DI
```

---

## Tecnologías y versiones

- Lenguaje: Kotlin
- Build: Gradle con Kotlin DSL
- Spring Boot: según `payflow-api/build.gradle.kts`
- Java: según `payflow-api/build.gradle.kts`

Antes de cambiar versiones, verificar compatibilidad con el resto del stack.

---

## Archivos que deben mantenerse sincronizados

- `course/00-roadmap/ROADMAP.md`
- `course/00-roadmap/INDEX.md`
- `README.md`
- `docs/api.md`
- `docs/architecture.md`
- `docs/database.md`

---

## Estilo de escritura

- Usar español para el material del curso.
- Usar inglés para código, nombres de clases, variables y endpoints de la API.
- Mantener un tono profesional pero accesible.
- Evitar emojis innecesarios en archivos de código o configuración.
