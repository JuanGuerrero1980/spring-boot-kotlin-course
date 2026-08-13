# Clase 10 — HTTP y REST

> **Fase:** 2 — REST API
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Explicar qué es HTTP y cómo funciona.
* Conocer los principales métodos HTTP y su semántica.
* Conocer los principales códigos de estado HTTP.
* Entender qué es REST y qué significa ser RESTful.
* Diseñar URLs de recursos de forma coherente.
* Aplicar estos conceptos a la API de PayFlow.

---

# 1. HTTP

HTTP significa:

> **Hypertext Transfer Protocol**

Es el protocolo que permite la comunicación entre clientes y servidores en la web.

Cuando una aplicación Android, una web o Postman hace una petición a nuestro backend, lo hace usando HTTP.

Una comunicación HTTP se basa en:

```text
Request  → el cliente pide algo
Response → el servidor responde
```

---

# 2. Estructura de una request

Una request HTTP tiene:

```text
METHOD PATH HTTP/VERSION
Headers

Body (opcional)
```

Ejemplo:

```http
POST /api/recharges HTTP/1.1
Host: localhost:8080
Content-Type: application/json

{
  "amount": 1000,
  "phoneNumber": "3531234567"
}
```

Partes:

* **Method:** `POST`
* **Path:** `/api/recharges`
* **Headers:** metadatos de la request
* **Body:** datos enviados al servidor

---

# 3. Estructura de una response

Una response HTTP tiene:

```text
HTTP/VERSION STATUS CODE
Headers

Body (opcional)
```

Ejemplo:

```http
HTTP/1.1 201 Created
Content-Type: application/json

{
  "id": 1,
  "status": "PENDING"
}
```

Partes:

* **Status code:** `201 Created`
* **Headers:** metadatos de la response
* **Body:** datos devueltos al cliente

---

# 4. Métodos HTTP

Cada método indica la intención de la operación.

| Método | Uso habitual |
|--------|--------------|
| `GET` | Obtener información |
| `POST` | Crear un recurso o iniciar una operación |
| `PUT` | Reemplazar un recurso completo |
| `PATCH` | Modificar parcialmente un recurso |
| `DELETE` | Eliminar un recurso |

---

# 5. GET

Se utiliza para obtener recursos.

```http
GET /api/recharges
```

```http
GET /api/recharges/1
```

En Spring:

```kotlin
@GetMapping
fun getAll(): List<RechargeResponse> { ... }

@GetMapping("/{id}")
fun getById(@PathVariable id: Long): RechargeResponse { ... }
```

`GET` no debería modificar el estado del servidor.

---

# 6. POST

Se utiliza para crear recursos o iniciar operaciones.

```http
POST /api/recharges
Content-Type: application/json

{
  "amount": 1000,
  "phoneNumber": "3531234567"
}
```

En Spring:

```kotlin
@PostMapping
fun create(@RequestBody request: CreateRechargeRequest): ResponseEntity<RechargeResponse> { ... }
```

Normalmente responde con `201 Created`.

---

# 7. PUT

Se utiliza para reemplazar un recurso completo.

```http
PUT /api/recharges/1
Content-Type: application/json

{
  "amount": 2000,
  "phoneNumber": "3531234567",
  "operator": "PERSONAL",
  "type": "PREPAID"
}
```

En Spring:

```kotlin
@PutMapping("/{id}")
fun update(@PathVariable id: Long, @RequestBody request: UpdateRechargeRequest): RechargeResponse { ... }
```

---

# 8. PATCH

Se utiliza para modificar parcialmente un recurso.

```http
PATCH /api/recharges/1
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

En Spring:

```kotlin
@PatchMapping("/{id}")
fun patch(@PathVariable id: Long, @RequestBody request: PatchRechargeRequest): RechargeResponse { ... }
```

---

# 9. DELETE

Se utiliza para eliminar un recurso.

```http
DELETE /api/recharges/1
```

En Spring:

```kotlin
@DeleteMapping("/{id}")
fun delete(@PathVariable id: Long): ResponseEntity<Unit> {
    rechargeService.delete(id)
    return ResponseEntity.noContent().build()
}
```

Normalmente responde con `204 No Content`.

---

# 10. Códigos de estado HTTP

Los códigos indican el resultado de la operación.

## 2xx — Éxito

```text
200 OK           → Operación exitosa
201 Created      → Recurso creado
204 No Content   → Operación exitosa sin body
```

## 4xx — Error del cliente

```text
400 Bad Request      → Request mal formada
401 Unauthorized     → No autenticado
403 Forbidden        → Autenticado pero sin permisos
404 Not Found        → Recurso no encontrado
409 Conflict         → Conflicto con el estado actual
422 Unprocessable Entity → Request válida pero lógicamente incorrecta
```

## 5xx — Error del servidor

```text
500 Internal Server Error → Error inesperado
503 Service Unavailable   → Servicio no disponible
```

---

# 11. ¿Qué es REST?

REST significa:

> **Representational State Transfer**

REST no es una librería ni un framework. Es un estilo arquitectónico para diseñar APIs.

Las ideas principales son:

* Trabajar con **recursos**.
* Cada recurso tiene una **URL**.
* Usar los **métodos HTTP** para operar sobre los recursos.
* Las respuestas representan el **estado** del recurso.

---

# 12. Recursos

Un recurso es cualquier cosa que pueda ser nombrada y manipulada.

En PayFlow API tenemos recursos como:

```text
recharges
accounts
users
payments
```

Cada recurso tiene una URL:

```text
/api/recharges
/api/accounts
/api/users
/api/payments
```

Un recurso específico se identifica con su ID:

```text
/api/recharges/1
/api/accounts/25
```

---

# 13. API RESTful

Una API es RESTful cuando sigue los principios REST de forma consistente.

Características:

* URLs que representan recursos.
* Uso correcto de métodos HTTP.
* Respuestas con códigos de estado apropiados.
* Stateless: cada request debe contener toda la información necesaria.

---

# 14. Diseño de endpoints en PayFlow

Un buen diseño para el recurso `recharges`:

```text
GET    /api/recharges          → listar recargas
POST   /api/recharges          → crear recarga
GET    /api/recharges/{id}     → obtener una recarga
PUT    /api/recharges/{id}     → reemplazar una recarga
PATCH  /api/recharges/{id}     → modificar parcialmente
DELETE /api/recharges/{id}     → eliminar una recarga
```

Un mal diseño sería:

```text
POST /api/createRecharge
POST /api/getRecharge
POST /api/deleteRecharge
```

En el buen diseño, la URL representa el recurso y el método HTTP representa la operación.

---

# 15. Stateless

Stateless significa que el servidor no guarda información del cliente entre requests.

Cada request debe ser autosuficiente. Si el cliente necesita autenticación, debe enviarla en cada request, por ejemplo mediante un header:

```http
Authorization: Bearer abc123
```

Esto facilita escalar la aplicación porque cualquier servidor puede atender cualquier request.

---

# Ejercicios

Resolvé estos ejercicios. Las respuestas están debajo de cada uno.

## Ejercicio 1

**Pregunta:** ¿Qué información contiene una request HTTP?

**Respuesta:**

Una request HTTP contiene:

* **Método:** indica la operación (GET, POST, etc.).
* **Path:** la URL del recurso.
* **Headers:** metadatos como `Content-Type` o `Authorization`.
* **Body:** datos enviados al servidor (opcional).

---

## Ejercicio 2

**Pregunta:** ¿Qué información contiene una response HTTP?

**Respuesta:**

Una response HTTP contiene:

* **Status code:** indica el resultado (200, 201, 404, 500, etc.).
* **Headers:** metadatos como `Content-Type`.
* **Body:** datos devueltos al cliente (opcional).

---

## Ejercicio 3

**Pregunta:** ¿Qué método HTTP usarías para cada operación?

1. Obtener una lista de usuarios.
2. Crear un nuevo usuario.
3. Actualizar completamente un usuario.
4. Cambiar solo el email de un usuario.
5. Eliminar un usuario.

**Respuesta:**

1. `GET /api/users`
2. `POST /api/users`
3. `PUT /api/users/{id}`
4. `PATCH /api/users/{id}`
5. `DELETE /api/users/{id}`

---

## Ejercicio 4

**Pregunta:** ¿Qué código de estado HTTP corresponde a cada situación?

1. Se creó correctamente una recarga.
2. Se obtuvo correctamente un usuario.
3. El cliente envió datos inválidos.
4. El recurso solicitado no existe.
5. Ocurrió un error inesperado en el servidor.

**Respuesta:**

1. `201 Created`
2. `200 OK`
3. `400 Bad Request`
4. `404 Not Found`
5. `500 Internal Server Error`

---

## Ejercicio 5

**Pregunta:** ¿Qué significa que una API sea RESTful?

**Respuesta:**

Una API es RESTful cuando:

* usa URLs para representar recursos;
* utiliza los métodos HTTP de forma semántica;
* devuelve códigos de estado apropiados;
* es stateless;
* mantiene un diseño consistente y predecible.

---

## Ejercicio 6

**Pregunta:** Diseñá los endpoints RESTful para el recurso `payments` (pagos) de PayFlow.

**Respuesta:**

```text
GET    /api/payments          → listar pagos
POST   /api/payments          → crear pago
GET    /api/payments/{id}     → obtener un pago
PUT    /api/payments/{id}     → reemplazar un pago
PATCH  /api/payments/{id}     → modificar parcialmente
DELETE /api/payments/{id}     → eliminar un pago
```

---

## Ejercicio 7

**Pregunta:** ¿Qué significa que una API sea stateless? ¿Por qué es importante?

**Respuesta:**

Stateless significa que el servidor no guarda información del cliente entre requests. Cada request debe contener toda la información necesaria.

Es importante porque facilita la escalabilidad: cualquier servidor puede atender cualquier request sin depender de un estado previo.

---

# Preguntas de entrevista

1. ¿Qué es HTTP?
2. ¿Qué partes tiene una request HTTP?
3. ¿Qué partes tiene una response HTTP?
4. ¿Cuál es la diferencia entre GET y POST?
5. ¿Cuál es la diferencia entre PUT y PATCH?
6. ¿Qué significa REST?
7. ¿Qué es una API RESTful?
8. ¿Qué significa que una API sea stateless?
9. ¿Qué código de estado devolverías al crear un recurso?
10. ¿Qué diferencia hay entre 401 y 403?

---

# Resumen

```text
HTTP
 ├── Request: method, path, headers, body
 └── Response: status code, headers, body

Métodos HTTP
 ├── GET    → obtener
 ├── POST   → crear
 ├── PUT    → reemplazar
 ├── PATCH  → modificar parcialmente
 └── DELETE → eliminar

REST
 ├── Recursos con URLs
 ├── Métodos HTTP semánticos
 ├── Códigos de estado apropiados
 └── Stateless
```

---

# Checklist

* [x] Entiendo qué es HTTP.
* [x] Entiendo las partes de una request.
* [x] Entiendo las partes de una response.
* [x] Conozco los principales métodos HTTP.
* [x] Conozco los principales códigos de estado.
* [x] Entiendo qué es REST.
* [x] Entiendo qué es una API RESTful.
* [x] Entiendo el concepto de stateless.
* [x] Puedo diseñar endpoints REST.
* [x] Completé los ejercicios.
* [x] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 11 — Request y Response

Vamos a profundizar en cada parte de una request y una response, y ver cómo Spring nos permite acceder a ellas.
