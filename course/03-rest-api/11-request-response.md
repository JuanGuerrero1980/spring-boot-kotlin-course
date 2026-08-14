# Clase 11 — Request y Response

> **Fase:** 2 — REST API
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Identificar todas las partes de una request HTTP.
* Identificar todas las partes de una response HTTP.
* Conocer los headers más comunes y su propósito.
* Leer headers de una request en Spring Boot.
* Construir responses con headers personalizados usando `ResponseEntity`.
* Entender cómo Spring serializa y deserializa JSON con Jackson.
* Aplicar estos conceptos en PayFlow API.

---

# 1. Request HTTP en detalle

Una request HTTP es el mensaje que el cliente envía al servidor.

Tiene la siguiente estructura:

```text
METHOD PATH HTTP/VERSION
Header-1: valor
Header-2: valor

Body (opcional)
```

Ejemplo:

```http
POST /api/recharges HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Accept: application/json
Authorization: Bearer abc123
Content-Length: 58

{
  "amount": 1000,
  "phoneNumber": "3531234567"
}
```

Partes:

* **Línea de solicitud:** `POST /api/recharges HTTP/1.1`
  * Método: `POST`
  * Path: `/api/recharges`
  * Versión HTTP: `HTTP/1.1`
* **Headers:** metadatos que describen la request.
* **Body:** datos enviados al servidor, opcional.

---

# 2. Response HTTP en detalle

Una response HTTP es el mensaje que el servidor devuelve al cliente.

Tiene la siguiente estructura:

```text
HTTP/VERSION STATUS CODE MESSAGE
Header-1: valor
Header-2: valor

Body (opcional)
```

Ejemplo:

```http
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/recharges/1

{
  "id": 1,
  "amount": 1000,
  "phoneNumber": "3531234567",
  "operator": "PERSONAL",
  "type": "PREPAID",
  "status": "PENDING"
}
```

Partes:

* **Línea de estado:** `HTTP/1.1 201 Created`
  * Versión HTTP: `HTTP/1.1`
  * Código de estado: `201`
  * Mensaje de estado: `Created`
* **Headers:** metadatos que describen la response.
* **Body:** datos devueltos al cliente, opcional.

---

# 3. Headers HTTP comunes

Los headers son pares `nombre: valor` que transportan metadatos.

## En la request

| Header | Propósito |
|--------|-----------|
| `Content-Type` | Indica el formato del body enviado. Ejemplo: `application/json`. |
| `Accept` | Indica qué formatos de respuesta acepta el cliente. Ejemplo: `application/json`. |
| `Authorization` | Envía credenciales o tokens. Ejemplo: `Bearer abc123`. |
| `User-Agent` | Identifica al cliente. Ejemplo: `PostmanRuntime/7.0`. |
| `Host` | Indica el dominio y puerto al que se hace la petición. |

## En la response

| Header | Propósito |
|--------|-----------|
| `Content-Type` | Indica el formato del body devuelto. |
| `Location` | Indica la URL del recurso creado. Ejemplo: `/api/recharges/1`. |
| `X-Request-Id` | Identificador único de la request, útil para trazabilidad. |

> **Nota:** los headers que comienzan con `X-` son headers personalizados. Aunque el estándar ya no los considera necesarios, siguen siendo muy usados.

---

# 4. Leer el request completo en Spring

Spring permite acceder a la request original a través de `HttpServletRequest`.

```kotlin
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DebugController {

    @GetMapping("/api/debug")
    fun debug(request: HttpServletRequest): Map<String, String> {
        return mapOf(
            "method" to request.method,
            "path" to request.requestURI,
            "contentType" to (request.contentType ?: "none")
        )
    }
}
```

`HttpServletRequest` es útil cuando necesitamos información de bajo nivel, como headers dinámicos o la IP del cliente.

---

# 5. Leer headers con `@RequestHeader`

Para leer un header específico usamos `@RequestHeader`.

```kotlin
@GetMapping("/api/greet")
fun greet(
    @RequestHeader("Accept-Language", required = false) language: String?
): String {
    return if (language == "es") {
        "Hola"
    } else {
        "Hello"
    }
}
```

Si el header puede no estar presente, lo marcamos como `required = false` y usamos un tipo nullable.

---

# 6. Construir responses en Spring

Spring ofrece dos formas principales de construir una response:

## Devolver un objeto directamente

```kotlin
@GetMapping("/api/hello")
fun hello(): HelloResponse {
    return HelloResponse(message = "Hello from PayFlow API")
}
```

Spring serializa el objeto a JSON y devuelve `200 OK`.

## Usar `ResponseEntity`

```kotlin
@GetMapping("/api/status")
fun status(): ResponseEntity<StatusResponse> {
    val body = StatusResponse(status = "UP", application = "PayFlow API")
    return ResponseEntity.ok(body)
}
```

`ResponseEntity` permite controlar status, headers y body.

---

# 7. `ResponseEntity` con headers personalizados

Podemos agregar headers personalizados a la response:

```kotlin
@PostMapping
fun create(@RequestBody request: CreateRechargeRequest): ResponseEntity<RechargeResponse> {
    val response = rechargeService.create(request)

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header("X-Request-Id", UUID.randomUUID().toString())
        .header("Location", "/api/recharges/${response.id}")
        .body(response)
}
```

Esto devuelve:

* status `201 Created`;
* header `X-Request-Id` con un identificador único;
* header `Location` con la URL del nuevo recurso;
* body con la recarga creada.

---

# 8. Serialización y deserialización JSON

Spring Boot usa **Jackson** por defecto para convertir objetos Kotlin a JSON y viceversa.

## Deserialización: JSON → objeto

```http
POST /api/recharges
Content-Type: application/json

{
  "amount": 1000,
  "phoneNumber": "3531234567"
}
```

```kotlin
@PostMapping
fun create(@RequestBody request: CreateRechargeRequest): RechargeResponse { ... }
```

Jackson convierte el JSON en una instancia de `CreateRechargeRequest`.

## Serialización: objeto → JSON

```kotlin
@GetMapping("/{id}")
fun findById(@PathVariable id: Long): ResponseEntity<RechargeResponse> { ... }
```

Jackson convierte la instancia de `RechargeResponse` en JSON.

### Requisitos para que funcione

* Las clases deben tener propiedades accesibles.
* Los nombres de las propiedades Kotlin deben coincidir con los campos del JSON.
* Los tipos deben ser compatibles.

---

# 9. Implementación en PayFlow API

Vamos a agregar un endpoint de ejemplo en `PayFlowController` para practicar la lectura y devolución de headers.

## Endpoint `/api/headers`

```kotlin
package com.payflow.api.controller

import com.payflow.api.dto.HelloResponse
import com.payflow.api.dto.StatusResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class PayFlowController {

    @GetMapping("/api/hello")
    fun hello(): HelloResponse {
        return HelloResponse(
            message = "Hello from PayFlow API"
        )
    }

    @GetMapping("/api/status")
    fun status(): StatusResponse {
        return StatusResponse(
            status = "UP",
            application = "PayFlow API"
        )
    }

    @GetMapping("/api/headers")
    fun headers(
        request: HttpServletRequest,
        @RequestHeader("X-Request-Id", required = false) requestId: String?
    ): ResponseEntity<Map<String, String?>> {
        val responseBody = mapOf(
            "method" to request.method,
            "path" to request.requestURI,
            "contentType" to request.contentType,
            "receivedRequestId" to requestId
        )

        return ResponseEntity
            .ok()
            .header("X-Response-Id", requestId ?: UUID.randomUUID().toString())
            .body(responseBody)
    }
}
```

### Qué hace este endpoint

* Lee el request completo mediante `HttpServletRequest`.
* Lee el header `X-Request-Id` mediante `@RequestHeader`.
* Devuelve un `ResponseEntity` con:
  * status `200 OK`;
  * header `X-Response-Id`;
  * body con información de la request.

### Ejemplo de uso

```http
GET /api/headers HTTP/1.1
Host: localhost:8080
X-Request-Id: abc-123
```

Response:

```http
HTTP/1.1 200 OK
Content-Type: application/json
X-Response-Id: abc-123

{
  "method": "GET",
  "path": "/api/headers",
  "contentType": null,
  "receivedRequestId": "abc-123"
}
```

---

# 10. Buenas prácticas

* Usa `ResponseEntity` cuando necesites controlar status o headers.
* Devuelve siempre `Content-Type` adecuado. Spring lo hace automáticamente con JSON.
* Usa headers personalizados como `X-Request-Id` para trazabilidad entre microservicios.
* Devuelve el header `Location` al crear recursos.
* Mantén los headers relevantes y evita devolver información interna innecesaria.
* Valida el formato de los datos antes de procesarlos.

---

# Ejercicios

Resolvé estos ejercicios en tu cuaderno. Las respuestas están en `exercises/solutions/11-request-response-solutions.md`.

## Ejercicio 1

¿Cuáles son las tres partes de una request HTTP? ¿Y de una response HTTP?

---

## Ejercicio 2

¿Para qué sirve el header `Content-Type`? ¿En qué se diferencia de `Accept`?

---

## Ejercicio 3

¿Qué header se usa comúnmente para enviar un token de autenticación? Escribí un ejemplo.

---

## Ejercicio 4

¿Qué ventaja tiene usar `ResponseEntity` en lugar de devolver el objeto directamente?

---

## Ejercicio 5

Explicá qué hace `@RequestHeader` y cuándo conviene marcarlo como `required = false`.

---

## Ejercicio 6

Diseñá una response para la creación de una recarga que incluya:

* status `201 Created`;
* body con la recarga creada;
* header `Location` con la URL del nuevo recurso;
* header `X-Request-Id` con un identificador único.

---

## Ejercicio 7

Implementá un endpoint `GET /api/user-agent` en PayFlow API que lea el header `User-Agent` de la request y lo devuelva en el body. Si el header no está presente, debe devolver `"unknown"`.

---

# Preguntas de entrevista

1. ¿Qué partes tiene una request HTTP?
2. ¿Qué partes tiene una response HTTP?
3. ¿Para qué sirve el header `Content-Type`?
4. ¿Cuál es la diferencia entre `Content-Type` y `Accept`?
5. ¿Cómo lees un header en Spring Boot?
6. ¿Cómo devuelves un header personalizado en una response?
7. ¿Qué es `ResponseEntity` y por qué usarlo?
8. ¿Qué librería usa Spring Boot para convertir objetos a JSON?
9. ¿Qué header devolverías al crear un recurso para indicar su ubicación?
10. ¿Qué es `HttpServletRequest` y cuándo lo usarías?

---

# Resumen

```text
Request HTTP
 ├── Línea de solicitud: METHOD PATH HTTP/VERSION
 ├── Headers: metadatos
 └── Body: datos opcionales

Response HTTP
 ├── Línea de estado: HTTP/VERSION STATUS MESSAGE
 ├── Headers: metadatos
 └── Body: datos opcionales

Headers comunes
 ├── Content-Type → formato del body
 ├── Accept → formato de respuesta esperado
 ├── Authorization → credenciales
 ├── Location → URL del recurso creado
 └── X-Request-Id → trazabilidad

Spring Boot
 ├── @RequestHeader → leer header
 ├── HttpServletRequest → acceder a la request completa
 ├── ResponseEntity → controlar status, headers y body
 └── Jackson → serializar/deserializar JSON
```

---

# Checklist

* [x] Entiendo las partes de una request HTTP.
* [x] Entiendo las partes de una response HTTP.
* [x] Conozco los headers más comunes.
* [x] Sé leer headers con `@RequestHeader`.
* [x] Sé usar `HttpServletRequest` cuando es necesario.
* [x] Sé construir responses con `ResponseEntity`.
* [x] Sé agregar headers personalizados a una response.
* [x] Entiendo cómo funciona la serialización JSON en Spring.
* [x] Completé los ejercicios.
* [x] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 12 — DTOs

Vamos a profundizar en los Data Transfer Objects: por qué separar los objetos de request y response del modelo de dominio, y cómo implementarlos correctamente en PayFlow API.
