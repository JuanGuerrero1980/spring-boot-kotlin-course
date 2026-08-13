# Clase 9 — Controllers

> **Fase:** 2 — REST API
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Entender el rol de un Controller en una aplicación Spring.
* Usar `@RestController` y `@RequestMapping`.
* Crear endpoints con `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping` y `@DeleteMapping`.
* Recibir datos con `@PathVariable`, `@RequestParam` y `@RequestBody`.
* Devolver respuestas con `ResponseEntity` y códigos HTTP adecuados.
* Diseñar endpoints REST coherentes para recursos.
* Aplicar estos conceptos en PayFlow API.

---

# 1. ¿Qué es un Controller?

Un Controller es la capa de entrada de nuestra aplicación.

Su trabajo principal es:

```text
Recibir request HTTP
        ↓
Interpretar parámetros y body
        ↓
Delegar al Service
        ↓
Devolver response HTTP
```

El Controller no debe tener lógica de negocio compleja. Esa responsabilidad pertenece al Service.

---

# 2. `@RestController`

```kotlin
@RestController
class RechargeController
```

Esta anotación le indica a Spring dos cosas:

1. La clase es un Bean de tipo Controller.
2. Los valores retornados por sus métodos se serializan directamente como respuesta HTTP (normalmente JSON).

`@RestController` es equivalente a:

```text
@Controller + @ResponseBody
```

---

# 3. `@RequestMapping`

Permite definir un prefijo común para todos los endpoints de un Controller.

```kotlin
@RestController
@RequestMapping("/api/recharges")
class RechargeController
```

Ahora todos los métodos del Controller tendrán `/api/recharges` como base.

---

# 4. Métodos HTTP y anotaciones

Spring proporciona anotaciones para cada método HTTP:

```text
@GetMapping     → GET
@PostMapping    → POST
@PutMapping     → PUT
@PatchMapping   → PATCH
@DeleteMapping  → DELETE
```

Ejemplo:

```kotlin
@GetMapping
fun getAll(): List<RechargeResponse> { ... }

@PostMapping
fun create(@RequestBody request: CreateRechargeRequest): RechargeResponse { ... }
```

Con el prefijo `@RequestMapping("/api/recharges")`, estos métodos responden a:

```text
GET    /api/recharges
POST   /api/recharges
```

---

# 5. `@PathVariable`

Cuando el ID de un recurso forma parte de la URL:

```http
GET /api/recharges/10
```

Usamos `@PathVariable`:

```kotlin
@GetMapping("/{id}")
fun getById(@PathVariable id: Long): RechargeResponse { ... }
```

Spring extrae el valor `10` y lo asigna al parámetro `id`.

---

# 6. `@RequestParam`

Cuando los valores van en el query string:

```http
GET /api/recharges?status=PENDING
```

Usamos `@RequestParam`:

```kotlin
@GetMapping
fun getAll(@RequestParam status: RechargeStatus?): List<RechargeResponse> { ... }
```

Podemos marcar parámetros como opcionales:

```kotlin
@RequestParam(required = false) status: RechargeStatus?
```

---

# 7. `@RequestBody`

Cuando los datos van en el body de la request, por ejemplo en un POST:

```http
POST /api/recharges
Content-Type: application/json

{
  "amount": 1000,
  "phoneNumber": "3531234567",
  "operator": "PERSONAL",
  "type": "PREPAID"
}
```

Usamos `@RequestBody`:

```kotlin
@PostMapping
fun create(@RequestBody request: CreateRechargeRequest): RechargeResponse { ... }
```

Spring deserializa el JSON en un objeto Kotlin usando Jackson.

---

# 8. `ResponseEntity`

`ResponseEntity` nos permite controlar explícitamente:

* status code;
* headers;
* body.

Ejemplo:

```kotlin
@GetMapping("/{id}")
fun getById(@PathVariable id: Long): ResponseEntity<RechargeResponse> {
    val response = rechargeService.findById(id)
        ?: return ResponseEntity.notFound().build()

    return ResponseEntity.ok(response)
}
```

Para crear un recurso:

```kotlin
@PostMapping
fun create(@RequestBody request: CreateRechargeRequest): ResponseEntity<RechargeResponse> {
    val response = rechargeService.create(request)
    return ResponseEntity.status(HttpStatus.CREATED).body(response)
}
```

---

# 9. Diseño RESTful de endpoints

Una buena API REST representa recursos y usa los métodos HTTP para operar sobre ellos.

Para el recurso `recharges`:

```text
GET    /api/recharges          → listar recargas
POST   /api/recharges          → crear recarga
GET    /api/recharges/{id}     → obtener una recarga
PATCH  /api/recharges/{id}     → modificar parcialmente
DELETE /api/recharges/{id}     → eliminar una recarga
```

La URL representa el recurso. El método HTTP representa la operación.

---

# 10. Implementación actual en PayFlow API

Actualmente tenemos:

```kotlin
@RestController
@RequestMapping("/api/recharges")
class RechargeController(
    private val rechargeService: RechargeService
) {

    @PostMapping
    fun create(@RequestBody request: CreateRechargeRequest): ResponseEntity<RechargeResponse> { ... }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): ResponseEntity<RechargeResponse> { ... }
}
```

El Controller:

* recibe requests HTTP;
* delega en `RechargeService`;
* devuelve respuestas con códigos de estado apropiados.

---

# 11. Buenas prácticas

* Mantener el Controller libre de lógica de negocio.
* Usar DTOs para request y response.
* Devolver códigos HTTP coherentes: `200`, `201`, `204`, `400`, `404`, etc.
* Usar prefijos con `@RequestMapping` para mantener endpoints organizados.
* No exponer entidades JPA directamente.
* Validar entrada en el Service o con anotaciones de validación.

---

# Ejercicios

Resolvé estos ejercicios en tu cuaderno. Las respuestas están debajo de cada uno para que puedas comparar.

## Ejercicio 1

**Pregunta:** ¿Qué responsabilidad tiene un Controller en una aplicación Spring?

**Respuesta:**

El Controller es la capa de entrada de la aplicación. Su responsabilidad principal es:

* recibir requests HTTP;
* interpretar parámetros, headers y body;
* delegar el procesamiento al Service;
* devolver responses HTTP con el código de estado adecuado.

No debe contener lógica de negocio compleja.

---

## Ejercicio 2

**Pregunta:** ¿Qué diferencia hay entre `@Controller` y `@RestController`?

**Respuesta:**

`@Controller` se usa en aplicaciones Spring MVC donde las vistas se resuelven con tecnologías como Thymeleaf o JSP.

`@RestController` combina `@Controller` y `@ResponseBody`. Indica que los métodos del Controller devuelven datos que se serializan directamente en la respuesta HTTP, normalmente JSON.

Para APIs REST usamos `@RestController`.

---

## Ejercicio 3

**Pregunta:** Tenemos:

```kotlin
@RestController
@RequestMapping("/api/products")
class ProductController {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ProductResponse { ... }
}
```

¿Qué URL responde este endpoint? ¿Qué parte es el path variable?

**Respuesta:**

La URL completa es:

```text
GET /api/products/{id}
```

Por ejemplo:

```text
GET /api/products/10
```

El path variable es `{id}`. Spring extrae el valor `10` y lo asigna al parámetro `id`.

---

## Ejercicio 4

**Pregunta:** ¿Cuándo usamos `@RequestParam` y cuándo `@PathVariable`?

**Respuesta:**

* `@PathVariable` se usa cuando el valor forma parte de la URL:

```text
GET /api/products/10
```

```kotlin
@GetMapping("/{id}")
fun getById(@PathVariable id: Long)
```

* `@RequestParam` se usa cuando el valor va en el query string:

```text
GET /api/products?category=ELECTRONICS
```

```kotlin
@GetMapping
fun getAll(@RequestParam category: String)
```

---

## Ejercicio 5

**Pregunta:** ¿Qué hace `@RequestBody`?

**Respuesta:**

`@RequestBody` indica que Spring debe tomar el cuerpo de la request HTTP, generalmente en formato JSON, y deserializarlo en un objeto Kotlin.

Ejemplo:

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
fun create(@RequestBody request: CreateRechargeRequest)
```

Spring convierte el JSON en una instancia de `CreateRechargeRequest`.

---

## Ejercicio 6

**Pregunta:** ¿Por qué es preferible usar `ResponseEntity` en lugar de devolver directamente el objeto?

**Respuesta:**

`ResponseEntity` permite controlar explícitamente:

* el código de estado HTTP;
* los headers;
* el body.

Esto es importante para devolver respuestas correctas como `201 Created`, `404 Not Found` o `204 No Content`.

Ejemplo:

```kotlin
@PostMapping
fun create(@RequestBody request: CreateRechargeRequest): ResponseEntity<RechargeResponse> {
    val response = rechargeService.create(request)
    return ResponseEntity.status(HttpStatus.CREATED).body(response)
}
```

---

## Ejercicio 7

**Pregunta:** Diseñá los endpoints REST para el recurso `accounts`.

**Respuesta:**

```text
GET    /api/accounts          → listar cuentas
POST   /api/accounts          → crear cuenta
GET    /api/accounts/{id}     → obtener cuenta por ID
PUT    /api/accounts/{id}     → reemplazar cuenta completa
PATCH  /api/accounts/{id}     → modificar parcialmente
DELETE /api/accounts/{id}     → eliminar cuenta
```

---

## Ejercicio 8

**Pregunta:** Agregá un endpoint `GET /api/recharges` al Controller de PayFlow API para listar todas las recargas. ¿Qué método agregarías en `RechargeService` y `RechargeRepository`?

**Respuesta:**

### Repository

```kotlin
@Repository
class RechargeRepository {

    private val storage = ConcurrentHashMap<Long, Recharge>()

    fun findAll(): List<Recharge> {
        return storage.values.toList()
    }

    // ... resto de métodos
}
```

### Service

```kotlin
@Service
class RechargeService(
    private val rechargeRepository: RechargeRepository
) {

    fun findAll(): List<RechargeResponse> {
        return rechargeRepository.findAll().map { it.toResponse() }
    }

    // ... resto de métodos
}
```

### Controller

```kotlin
@RestController
@RequestMapping("/api/recharges")
class RechargeController(
    private val rechargeService: RechargeService
) {

    @GetMapping
    fun getAll(): List<RechargeResponse> {
        return rechargeService.findAll()
    }

    // ... resto de endpoints
}
```

---

# Preguntas de entrevista

1. ¿Qué es un Controller en Spring?
2. ¿Qué hace `@RestController`?
3. ¿Para qué sirve `@RequestMapping`?
4. ¿Qué diferencia hay entre `@PathVariable` y `@RequestParam`?
5. ¿Qué hace `@RequestBody`?
6. ¿Qué ventajas tiene usar `ResponseEntity`?
7. ¿Cómo diseñarías un endpoint REST para crear un recurso?

---

# Resumen

```text
@RestController
   │
   └── Define un Controller que devuelve datos serializados

@RequestMapping("/api/recharges")
   │
   └── Define el prefijo de los endpoints

@GetMapping / @PostMapping / @PutMapping / @PatchMapping / @DeleteMapping
   │
   └── Mapean métodos HTTP

@PathVariable
   │
   └── Obtiene valores del path

@RequestParam
   │
   └── Obtiene valores del query string

@RequestBody
   │
   └── Deserializa el body JSON a un objeto Kotlin

ResponseEntity
   │
   └── Permite controlar status, headers y body
```

---

# Checklist

* [x] Entiendo el rol del Controller.
* [x] Sé usar `@RestController`.
* [x] Sé usar `@RequestMapping`.
* [x] Sé usar `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, `@DeleteMapping`.
* [x] Entiendo `@PathVariable`.
* [x] Entiendo `@RequestParam`.
* [x] Entiendo `@RequestBody`.
* [x] Sé usar `ResponseEntity`.
* [x] Puedo diseñar endpoints REST.
* [x] Completé los ejercicios.
* [x] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 10 — HTTP y REST

Vamos a profundizar en el protocolo HTTP, los métodos, los códigos de estado y los principios REST.
