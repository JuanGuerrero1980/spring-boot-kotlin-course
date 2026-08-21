# Soluciones — Clase 13: Validation

> **Importante:** estas soluciones son de referencia. Es recomendable intentar los ejercicios primero y luego comparar.

---

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
