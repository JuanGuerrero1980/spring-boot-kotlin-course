# Soluciones — Clase 12: DTOs

> **Importante:** estas soluciones son de referencia. Es recomendable intentar los ejercicios primero y luego comparar.

---

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
