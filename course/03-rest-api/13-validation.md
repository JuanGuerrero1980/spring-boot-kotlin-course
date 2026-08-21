# Clase 13 — Validation

> **Fase:** 2 — REST API
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Entender qué es Bean Validation y cómo funciona en Spring Boot.
* Conocer las anotaciones de validación más comunes.
* Aplicar validaciones en DTOs de request.
* Usar `@Valid` para activar la validación en los controllers.
* Interpretar la respuesta de error que Spring devuelve por defecto.
* Aplicar validaciones en PayFlow API.

---

# 1. ¿Qué es Bean Validation?

Bean Validation es una especificación de Java que permite definir reglas de validación directamente sobre las clases mediante anotaciones.

Spring Boot la integra automáticamente para validar los datos de entrada antes de que lleguen al Service.

```text
Request JSON
     ↓
Jackson lo convierte en DTO
     ↓
Spring valida el DTO con @Valid
     ↓
Si falla → 400 Bad Request
Si pasa  → continúa al Service
```

---

# 2. Dependencia necesaria

En `build.gradle.kts` agregamos:

```kotlin
implementation("org.springframework.boot:spring-boot-starter-validation")
```

Esta dependencia incluye Hibernate Validator, la implementación más usada de Bean Validation.

---

# 3. Anotaciones de validación comunes

| Anotación | Uso |
|-----------|-----|
| `@NotNull` | El valor no puede ser `null`. |
| `@NotBlank` | Para strings: no puede ser `null`, vacío ni solo espacios. |
| `@NotEmpty` | Para strings, listas, mapas: no puede ser `null` ni vacío. |
| `@Positive` | El número debe ser mayor que cero. |
| `@PositiveOrZero` | El número debe ser mayor o igual a cero. |
| `@Size(min, max)` | Longitud de string o tamaño de colección. |
| `@Min(value)` | Valor numérico mínimo. |
| `@Max(value)` | Valor numérico máximo. |
| `@Email` | Formato de email válido. |
| `@Pattern(regex)` | Debe coincidir con la expresión regular. |

---

# 4. Validación en Kotlin: `@field:`

En Kotlin las anotaciones se aplican a diferentes targets posibles. Para Bean Validation, debemos usar `@field:` para que la anotación se aplique al campo generado.

```kotlin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateUserRequest(
    @field:NotBlank
    val email: String,

    @field:Positive
    val age: Int
)
```

Sin `@field:`, la anotación puede no aplicarse correctamente y la validación no funcionará.

---

# 5. Activar la validación con `@Valid`

En el Controller usamos `@Valid` antes de `@RequestBody`:

```kotlin
@PostMapping
fun create(
    @Valid @RequestBody request: CreateUserRequest
): ResponseEntity<UserResponse> { ... }
```

Si el DTO no cumple las reglas, Spring lanza `MethodArgumentNotValidException` y devuelve `400 Bad Request` automáticamente.

---

# 6. Respuesta de error por defecto

Si falla la validación, Spring devuelve algo como:

```json
{
  "timestamp": "2026-08-21T10:15:30.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for object='createRechargeRequest'. Error count: 2",
  "errors": [
    {
      "field": "amount",
      "defaultMessage": "must be greater than 0"
    },
    {
      "field": "phoneNumber",
      "defaultMessage": "must not be blank"
    }
  ],
  "path": "/api/recharges"
}
```

En la clase 14 veremos cómo personalizar este formato con un manejador global de excepciones.

---

# 7. Implementación en PayFlow API

Vamos a agregar validaciones a los DTOs de recargas.

## `CreateRechargeRequest`

```kotlin
package com.payflow.api.dto

import com.payflow.api.domain.Operator
import com.payflow.api.domain.RechargeType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class CreateRechargeRequest(
    @field:Positive
    val amount: BigDecimal,

    @field:NotBlank
    val phoneNumber: String,

    @field:NotNull
    val operator: Operator,

    @field:NotNull
    val type: RechargeType
)
```

## `UpdateRechargeRequest`

Como es un reemplazo completo, también validamos todos los campos:

```kotlin
data class UpdateRechargeRequest(
    @field:Positive
    val amount: BigDecimal,

    @field:NotBlank
    val phoneNumber: String,

    @field:NotNull
    val operator: Operator,

    @field:NotNull
    val type: RechargeType
)
```

## `PatchRechargeRequest`

```kotlin
data class PatchRechargeRequest(
    @field:NotNull
    val status: RechargeStatus
)
```

## Controller

Agregamos `@Valid` en los métodos que reciben request bodies:

```kotlin
@PostMapping
fun create(
    @Valid @RequestBody request: CreateRechargeRequest
): ResponseEntity<RechargeResponse> { ... }

@PutMapping("/{id}")
fun replace(
    @PathVariable id: Long,
    @Valid @RequestBody request: UpdateRechargeRequest
): ResponseEntity<RechargeResponse> { ... }

@PatchMapping("/{id}")
fun patch(
    @PathVariable id: Long,
    @Valid @RequestBody request: PatchRechargeRequest
): ResponseEntity<RechargeResponse> { ... }
```

## Limpieza del Service

Con Bean Validation, las validaciones básicas ya no necesitan estar en el Service:

```kotlin
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
```

Las reglas de negocio más complejas siguen en el Service.

---

# 8. Buenas prácticas

* Usa `@field:` en Kotlin para aplicar las anotaciones al campo.
* Coloca las validaciones básicas en los request DTOs, no en el Service.
* Usa `@Valid` siempre que recibas un request body que deba validarse.
* No dupliques validaciones: si el DTO ya valida, no hace falta volver a validar en el Service.
* Mantén mensajes de error claros. Podés personalizarlos con `message = "..."`.
* En la clase 14 aprenderemos a devolver respuestas de error consistentes.

---

# Ejercicios

Resolvé estos ejercicios en tu cuaderno. Las respuestas están debajo de cada uno y también en `exercises/solutions/13-validation-solutions.md`.

## Ejercicio 1

**Pregunta:** ¿Qué es Bean Validation y qué problema resuelve?

**Respuesta:**

Bean Validation es una especificación que permite definir reglas de validación sobre objetos usando anotaciones.

Resuelve el problema de validar los datos de entrada de forma declarativa, evitando escribir manualmente muchas verificaciones en el Service.

---

## Ejercicio 2

**Pregunta:** ¿Por qué en Kotlin usamos `@field:NotBlank` en lugar de `@NotBlank`?

**Respuesta:**

Porque en Kotlin una propiedad de `data class` puede tener el target de la anotación en distintos lugares (propiedad, getter, campo, etc.).

`@field:` fuerza a que la anotación se aplique al campo generado por el compilador, que es lo que Hibernate Validator lee.

---

## Ejercicio 3

**Pregunta:** ¿Qué anotación se usa en el Controller para activar la validación de un request body?

**Respuesta:**

`@Valid`. Se coloca antes de `@RequestBody`:

```kotlin
fun create(@Valid @RequestBody request: CreateRechargeRequest): RechargeResponse
```

---

## Ejercicio 4

**Pregunta:** ¿Qué código de estado HTTP devuelve Spring cuando falla una validación?

**Respuesta:**

`400 Bad Request`.

Además incluye en el body información sobre los campos que fallaron y los mensajes de error.

---

## Ejercicio 5

**Pregunta:** Escribí las anotaciones de validación adecuadas para este DTO:

```kotlin
data class CreateAccountRequest(
    val alias: String,
    val currency: String,
    val balance: BigDecimal
)
```

Considerá que:

* `alias` no puede estar vacío y debe tener entre 3 y 50 caracteres.
* `currency` no puede ser nulo.
* `balance` debe ser mayor o igual a cero.

**Respuesta:**

```kotlin
data class CreateAccountRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val alias: String,

    @field:NotNull
    val currency: String,

    @field:PositiveOrZero
    val balance: BigDecimal
)
```

---

## Ejercicio 6

**Pregunta:** ¿Qué pasa con las validaciones manuales que teníamos en `RechargeService.create()` después de agregar Bean Validation?

**Respuesta:**

Las validaciones básicas como `amount > 0` y `phoneNumber` no vacío pueden eliminarse del Service, porque ahora las hace Bean Validation automáticamente.

Las reglas de negocio más complejas, si las hay, deben seguir en el Service.

---

## Ejercicio 7

**Pregunta:** Implementá las validaciones en `CreateRechargeRequest`, `UpdateRechargeRequest` y `PatchRechargeRequest`, y agregá `@Valid` en los métodos correspondientes de `RechargeController`.

**Respuesta:**

### Dependencia

```kotlin
implementation("org.springframework.boot:spring-boot-starter-validation")
```

### DTOs

```kotlin
data class CreateRechargeRequest(
    @field:Positive
    val amount: BigDecimal,

    @field:NotBlank
    val phoneNumber: String,

    @field:NotNull
    val operator: Operator,

    @field:NotNull
    val type: RechargeType
)

data class UpdateRechargeRequest(
    @field:Positive
    val amount: BigDecimal,

    @field:NotBlank
    val phoneNumber: String,

    @field:NotNull
    val operator: Operator,

    @field:NotNull
    val type: RechargeType
)

data class PatchRechargeRequest(
    @field:NotNull
    val status: RechargeStatus
)
```

### Controller

```kotlin
@PostMapping
fun create(
    @Valid @RequestBody request: CreateRechargeRequest
): ResponseEntity<RechargeResponse> { ... }

@PutMapping("/{id}")
fun replace(
    @PathVariable id: Long,
    @Valid @RequestBody request: UpdateRechargeRequest
): ResponseEntity<RechargeResponse> { ... }

@PatchMapping("/{id}")
fun patch(
    @PathVariable id: Long,
    @Valid @RequestBody request: PatchRechargeRequest
): ResponseEntity<RechargeResponse> { ... }
```

---

# Preguntas de entrevista

1. ¿Qué es Bean Validation?
2. ¿Qué dependencia se necesita en Spring Boot?
3. ¿Para qué sirve `@Valid`?
4. ¿Por qué en Kotlin usamos `@field:` antes de las anotaciones de validación?
5. ¿Qué diferencia hay entre `@NotNull`, `@NotBlank` y `@NotEmpty`?
6. ¿Qué código de estado devuelve Spring cuando falla la validación?
7. ¿Dónde deberían ir las validaciones básicas: Controller, Service o DTO?
8. ¿Qué pasa si no agregás `@Valid` al request body?

---

# Resumen

```text
Bean Validation
 ├── Define reglas de validación con anotaciones
 ├── Se aplica a los request DTOs
 └── Se activa con @Valid en el Controller

Anotaciones comunes
 ├── @NotNull     → no null
 ├── @NotBlank    → string no vacío
 ├── @NotEmpty    → string/colección no vacío
 ├── @Positive    → número mayor a cero
 ├── @Size        → tamaño entre min y max
 ├── @Min / @Max  → valores numéricos límites
 └── @Email       → formato de email

Kotlin
 └── Usar @field: para aplicar al campo correcto

Spring Boot
 ├── Dependencia: spring-boot-starter-validation
 ├── @Valid activa la validación
 └── Falla → 400 Bad Request
```

---

# Checklist

* [x] Entiendo qué es Bean Validation.
* [x] Conozco las anotaciones de validación más comunes.
* [x] Sé usar `@field:` en Kotlin.
* [x] Sé usar `@Valid` en controllers.
* [x] Sé interpretar la respuesta de error por defecto.
* [x] Apliqué validaciones en PayFlow API.
* [x] Completé los ejercicios.
* [x] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 14 — Exception Handling

Vamos a aprender a manejar excepciones de forma global en la aplicación, devolviendo respuestas de error consistentes y personalizadas.
