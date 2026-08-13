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

Resolvé estos ejercicios en tu cuaderno.

## Ejercicio 1

¿Qué responsabilidad tiene un Controller en una aplicación Spring?

## Ejercicio 2

¿Qué diferencia hay entre `@Controller` y `@RestController`?

## Ejercicio 3

Tenemos:

```kotlin
@RestController
@RequestMapping("/api/products")
class ProductController {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ProductResponse { ... }
}
```

¿Qué URL responde este endpoint? ¿Qué parte es el path variable?

## Ejercicio 4

¿Cuándo usamos `@RequestParam` y cuándo `@PathVariable`?

## Ejercicio 5

¿Qué hace `@RequestBody`?

## Ejercicio 6

¿Por qué es preferible usar `ResponseEntity` en lugar de devolver directamente el objeto?

## Ejercicio 7

Diseñá los endpoints REST para el recurso `accounts` (cuentas de PayFlow). Incluí al menos listar, crear, obtener una y eliminar.

## Ejercicio 8

Agregá un endpoint `GET /api/recharges` al Controller de PayFlow API para listar todas las recargas. ¿Qué método agregarías en `RechargeService` y `RechargeRepository`?

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
