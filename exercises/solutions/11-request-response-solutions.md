# Soluciones — Clase 11: Request y Response

> **Importante:** estas soluciones son de referencia. Es recomendable intentar los ejercicios primero y luego comparar.

---

## Ejercicio 1

**Pregunta:** ¿Cuáles son las tres partes de una request HTTP? ¿Y de una response HTTP?

**Respuesta:**

Una request HTTP tiene:

* **Línea de solicitud:** método, path y versión HTTP.
* **Headers:** metadatos de la request.
* **Body:** datos enviados al servidor, opcional.

Una response HTTP tiene:

* **Línea de estado:** versión HTTP, código de estado y mensaje.
* **Headers:** metadatos de la response.
* **Body:** datos devueltos al cliente, opcional.

---

## Ejercicio 2

**Pregunta:** ¿Para qué sirve el header `Content-Type`? ¿En qué se diferencia de `Accept`?

**Respuesta:**

* `Content-Type` indica el formato del body que se está enviando en la request o devolviendo en la response. Ejemplo: `application/json`.
* `Accept` indica qué formatos de respuesta acepta el cliente. Ejemplo: `application/json`.

En una request:

```http
Content-Type: application/json  → "lo que envío es JSON"
Accept: application/json        → "quiero que me respondas en JSON"
```

---

## Ejercicio 3

**Pregunta:** ¿Qué header se usa comúnmente para enviar un token de autenticación? Escribí un ejemplo.

**Respuesta:**

El header `Authorization`.

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Ejercicio 4

**Pregunta:** ¿Qué ventaja tiene usar `ResponseEntity` en lugar de devolver el objeto directamente?

**Respuesta:**

`ResponseEntity` permite controlar explícitamente:

* el código de estado HTTP;
* los headers de la response;
* el body de la response.

Esto es necesario para devolver respuestas como `201 Created` con header `Location`, o `404 Not Found` sin body.

---

## Ejercicio 5

**Pregunta:** Explicá qué hace `@RequestHeader` y cuándo conviene marcarlo como `required = false`.

**Respuesta:**

`@RequestHeader` lee un header específico de la request HTTP y lo asigna a un parámetro del método.

Se marca como `required = false` cuando el header puede no estar presente. En ese caso, el parámetro debe ser nullable.

```kotlin
@GetMapping("/api/greet")
fun greet(
    @RequestHeader("Accept-Language", required = false) language: String?
): String { ... }
```

---

## Ejercicio 6

**Pregunta:** Diseñá una response para la creación de una recarga que incluya:

* status `201 Created`;
* body con la recarga creada;
* header `Location` con la URL del nuevo recurso;
* header `X-Request-Id` con un identificador único.

**Respuesta:**

```kotlin
@PostMapping
fun create(@RequestBody request: CreateRechargeRequest): ResponseEntity<RechargeResponse> {
    val response = rechargeService.create(request)

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header("Location", "/api/recharges/${response.id}")
        .header("X-Request-Id", UUID.randomUUID().toString())
        .body(response)
}
```

Response HTTP:

```http
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/recharges/1
X-Request-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479

{
  "id": 1,
  "amount": 1000,
  "phoneNumber": "3531234567",
  "operator": "PERSONAL",
  "type": "PREPAID",
  "status": "PENDING"
}
```

---

## Ejercicio 7

**Pregunta:** Implementá un endpoint `GET /api/user-agent` en PayFlow API que lea el header `User-Agent` de la request y lo devuelva en el body. Si el header no está presente, debe devolver `"unknown"`.

**Respuesta:**

```kotlin
@GetMapping("/api/user-agent")
fun userAgent(
    @RequestHeader("User-Agent", required = false) userAgent: String?
): Map<String, String> {
    return mapOf(
        "userAgent" to (userAgent ?: "unknown")
    )
}
```

También se puede implementar con `ResponseEntity`:

```kotlin
@GetMapping("/api/user-agent")
fun userAgent(
    @RequestHeader("User-Agent", required = false) userAgent: String?
): ResponseEntity<Map<String, String>> {
    return ResponseEntity.ok(
        mapOf("userAgent" to (userAgent ?: "unknown"))
    )
}
```

---
