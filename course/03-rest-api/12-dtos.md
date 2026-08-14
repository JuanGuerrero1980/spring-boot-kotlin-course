# Clase 12 — DTOs

> **Fase:** 2 — REST API
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Explicar qué es un DTO y para qué sirve.
* Diferenciar un request DTO de un response DTO.
* Entender por qué no exponer entidades de dominio o JPA directamente.
* Crear DTOs en Kotlin usando `data class`.
* Mapear objetos de dominio a DTOs y viceversa.
* Aplicar DTOs en PayFlow API.

---

# 1. ¿Qué es un DTO?

DTO significa **Data Transfer Object**.

Es un objeto que usamos para transportar datos entre capas de la aplicación o entre el backend y el cliente.

En una API REST, los DTOs representan la forma en que el cliente envía y recibe información.

```text
Cliente            Backend
   │                  │
   ├── Request DTO ──→│
   │                  │→ Service → Dominio → Repository
   │←── Response DTO ─┤
```

---

# 2. ¿Por qué usar DTOs?

Sin DTOs, podríamos exponer directamente las clases de dominio o las entidades JPA:

```kotlin
@GetMapping("/{id}")
fun findById(@PathVariable id: Long): Recharge { ... }
```

Esto genera varios problemas:

* **Acoplamiento:** el cliente conoce la estructura interna del dominio.
* **Seguridad:** podemos exponer campos sensibles sin querer.
* **Flexibilidad:** cualquier cambio en el dominio rompe el contrato de la API.
* **Validaciones:** las entidades de dominio no suelen tener las reglas de validación de entrada.

Los DTOs nos permiten definir un **contrato claro** entre el cliente y el backend.

---

# 3. Request DTO vs Response DTO

## Request DTO

Representa los datos que el cliente envía al servidor.

```kotlin
data class CreateRechargeRequest(
    val amount: BigDecimal,
    val phoneNumber: String,
    val operator: Operator,
    val type: RechargeType
)
```

## Response DTO

Representa los datos que el servidor devuelve al cliente.

```kotlin
data class RechargeResponse(
    val id: Long,
    val amount: BigDecimal,
    val phoneNumber: String,
    val operator: Operator,
    val type: RechargeType,
    val status: RechargeStatus
)
```

La misma entidad de dominio puede tener distintos DTOs según la operación.

---

# 4. DTOs en Kotlin: `data class`

Kotlin tiene `data class`, una forma concisa de crear clases cuyo propósito principal es contener datos.

```kotlin
data class HelloResponse(
    val message: String
)
```

Una `data class` genera automáticamente:

* `equals()` y `hashCode()`;
* `toString()`;
* `copy()`;
* destructuring (`component1()`, `component2()`, etc.).

### Ventajas para DTOs

* Código reducido.
* Inmutabilidad con `val`.
* Ideal para serialización JSON.

---

# 5. Mapeo entre dominio y DTOs

El Service recibe un request DTO, lo convierte a un objeto de dominio, lo procesa y devuelve un response DTO.

```kotlin
@Service
class RechargeService {

    fun create(request: CreateRechargeRequest): RechargeResponse {
        val recharge = Recharge(
            id = 0,
            amount = request.amount,
            phoneNumber = request.phoneNumber,
            operator = request.operator,
            type = request.type,
            status = RechargeStatus.PENDING
        )

        val saved = rechargeRepository.save(recharge)
        return saved.toResponse()
    }

    private fun Recharge.toResponse(): RechargeResponse {
        return RechargeResponse(
            id = id,
            amount = amount,
            phoneNumber = phoneNumber,
            operator = operator,
            type = type,
            status = status
        )
    }
}
```

Este mapeo puede hacerse con:

* funciones de extensión privadas dentro del Service;
* funciones de extensión públicas;
* clases Mapper (lo veremos en la clase 24).

---

# 6. DTOs en PayFlow API

Actualmente PayFlow API usa estos DTOs:

### Request DTOs

* `CreateRechargeRequest`: datos para crear una recarga.

### Response DTOs

* `RechargeResponse`: datos de una recarga devueltos al cliente.
* `HelloResponse`: mensaje del endpoint de prueba.
* `StatusResponse`: estado de la aplicación.

### Modelo de dominio

* `Recharge`: representa el concepto de recarga en el negocio.

El Controller nunca devuelve directamente una instancia de `Recharge`. Siempre usa `RechargeResponse`.

---

# 7. Agregando un DTO parcial: `PatchRechargeRequest`

A veces el cliente quiere modificar solo algunos campos de un recurso. Para eso usamos un DTO parcial.

En PayFlow API vamos a agregar un endpoint `PATCH /api/recharges/{id}` que permita cambiar el estado de una recarga.

## Nuevo DTO

```kotlin
package com.payflow.api.dto

import com.payflow.api.domain.RechargeStatus

data class PatchRechargeRequest(
    val status: RechargeStatus
)
```

## Cambios en el Service

```kotlin
fun patch(id: Long, request: PatchRechargeRequest): RechargeResponse? {
    val updated = rechargeRepository.updateStatus(id, request.status)
        ?: return null

    return updated.toResponse()
}
```

## Cambios en el Repository

```kotlin
fun updateStatus(id: Long, status: RechargeStatus): Recharge? {
    val existing = storage[id] ?: return null

    val updated = Recharge(
        id = existing.id,
        amount = existing.amount,
        phoneNumber = existing.phoneNumber,
        operator = existing.operator,
        type = existing.type,
        status = status
    )

    storage[id] = updated
    return updated
}
```

## Nuevo endpoint en el Controller

```kotlin
@PatchMapping("/{id}")
fun patch(
    @PathVariable id: Long,
    @RequestBody request: PatchRechargeRequest
): ResponseEntity<RechargeResponse> {
    val response = rechargeService.patch(id, request)
        ?: return ResponseEntity.notFound().build()

    return ResponseEntity.ok(response)
}
```

Con esto practicamos:

* crear un nuevo request DTO;
* usarlo para modificar parcialmente un recurso;
* devolver un response DTO;
* mantener el dominio separado del contrato de la API.

---

# 8. Buenas prácticas

* Usa `data class` para DTOs en Kotlin.
* Prefiere `val` sobre `var` para mantener inmutabilidad.
* No expongas entidades JPA ni clases de dominio directamente como responses.
* Usa DTOs de request y response distintos cuando el contrato lo requiera.
* Mantén los DTOs simples: solo datos, sin lógica de negocio.
* Coloca los DTOs en un paquete dedicado, por ejemplo `com.payflow.api.dto`.
* El mapeo entre dominio y DTOs debe hacerse en la capa Service o en un Mapper.

---

# Ejercicios

Resolvé estos ejercicios en tu cuaderno. Las respuestas están debajo de cada uno y también en `exercises/solutions/12-dtos-solutions.md`.

## Ejercicio 1

**Pregunta:** ¿Qué es un DTO y cuál es su propósito principal?

**Respuesta:**

Un DTO (*Data Transfer Object*) es un objeto que usamos para transportar datos entre capas de la aplicación o entre el backend y el cliente.

Su propósito principal es definir un contrato claro de datos, evitando exponer directamente entidades de dominio o JPA.

---

## Ejercicio 2

**Pregunta:** ¿Cuál es la diferencia entre un request DTO y un response DTO?

**Respuesta:**

* Un **request DTO** representa los datos que el cliente envía al servidor.
* Un **response DTO** representa los datos que el servidor devuelve al cliente.

Ambos pueden tener campos distintos según la operación.

---

## Ejercicio 3

**Pregunta:** ¿Por qué no deberíamos exponer directamente una entidad JPA como respuesta HTTP?

**Respuesta:**

Porque genera acoplamiento, puede exponer datos sensibles, dificulta cambiar el modelo de dominio sin romper el contrato de la API, y no permite adaptar la respuesta a lo que realmente necesita el cliente.

---

## Ejercicio 4

**Pregunta:** ¿Qué ventajas ofrece usar `data class` para DTOs en Kotlin?

**Respuesta:**

* Genera automáticamente `equals()`, `hashCode()`, `toString()` y `copy()`.
* Reduce el código boilerplate.
* Favorece la inmutabilidad al usar `val`.
* Es ideal para serialización y deserialización JSON.

---

## Ejercicio 5

**Pregunta:** Dado el siguiente request DTO:

```kotlin
data class CreateUserRequest(
    val email: String,
    val password: String,
    val fullName: String
)
```

¿Por qué no deberíamos devolver el mismo DTO como response al crear un usuario?

**Respuesta:**

Porque la response no debería incluir la contraseña. El response DTO debe tener solo los datos seguros y necesarios para el cliente, por ejemplo:

```kotlin
data class UserResponse(
    val id: Long,
    val email: String,
    val fullName: String
)
```

---

## Ejercicio 6

**Pregunta:** Escribí un request DTO y un response DTO para crear una cuenta (`Account`) en PayFlow API. La cuenta debe tener: `alias`, `currency` y `balance` inicial.

**Respuesta:**

```kotlin
data class CreateAccountRequest(
    val alias: String,
    val currency: String,
    val balance: BigDecimal
)

data class AccountResponse(
    val id: Long,
    val alias: String,
    val currency: String,
    val balance: BigDecimal
)
```

---

## Ejercicio 7

**Pregunta:** Implementá un endpoint `PATCH /api/recharges/{id}` en PayFlow API que reciba un `PatchRechargeRequest` y actualice el estado de la recarga. Si no existe, debe devolver `404 Not Found`.

**Respuesta:**

### DTO

```kotlin
data class PatchRechargeRequest(
    val status: RechargeStatus
)
```

### Repository

```kotlin
fun updateStatus(id: Long, status: RechargeStatus): Recharge? {
    val existing = storage[id] ?: return null

    val updated = Recharge(
        id = existing.id,
        amount = existing.amount,
        phoneNumber = existing.phoneNumber,
        operator = existing.operator,
        type = existing.type,
        status = status
    )

    storage[id] = updated
    return updated
}
```

### Service

```kotlin
fun patch(id: Long, request: PatchRechargeRequest): RechargeResponse? {
    val updated = rechargeRepository.updateStatus(id, request.status)
        ?: return null

    return updated.toResponse()
}
```

### Controller

```kotlin
@PatchMapping("/{id}")
fun patch(
    @PathVariable id: Long,
    @RequestBody request: PatchRechargeRequest
): ResponseEntity<RechargeResponse> {
    val response = rechargeService.patch(id, request)
        ?: return ResponseEntity.notFound().build()

    return ResponseEntity.ok(response)
}
```

---

# Preguntas de entrevista

1. ¿Qué es un DTO?
2. ¿Por qué es importante usar DTOs en una API REST?
3. ¿Cuál es la diferencia entre un request DTO y un response DTO?
4. ¿Por qué no deberías exponer entidades JPA directamente?
5. ¿Qué es una `data class` en Kotlin?
6. ¿Dónde debería hacerse el mapeo entre entidades y DTOs?
7. ¿Qué ventajas tiene la inmutabilidad en los DTOs?
8. ¿Cómo manejarías un endpoint que solo modifica algunos campos de un recurso?

---

# Resumen

```text
DTO (Data Transfer Object)
 ├── Define el contrato de datos con el cliente
 ├── Evita exponer dominio o JPA
 ├── Se implementa con data class en Kotlin
 ├── Puede ser de request o de response
 └── Se mapea desde/hacia el dominio en el Service

data class
 ├── Genera equals(), hashCode(), toString(), copy()
 ├── Favorece la inmutabilidad con val
 └── Ideal para serialización JSON
```

---

# Checklist

* [x] Entiendo qué es un DTO.
* [x] Sé diferenciar request DTO de response DTO.
* [x] Entiendo por qué no exponer entidades JPA ni dominio directamente.
* [x] Sé crear DTOs con `data class`.
* [x] Sé mapear entre dominio y DTOs.
* [x] Completé los ejercicios.
* [x] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 13 — Validation

Vamos a aprender a validar los datos de entrada usando Bean Validation con anotaciones como `@NotBlank`, `@Positive` y `@Valid`.
