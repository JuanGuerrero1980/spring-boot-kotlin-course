:# Soluciones — Clase 9: Controllers

> **Importante:** estas soluciones son de referencia. Es recomendable intentar los ejercicios primero y luego comparar.

---

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

**Pregunta:** Agregá un endpoint `GET /api/recharges` para listar todas las recargas. ¿Qué método agregarías en `RechargeService` y `RechargeRepository`?

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

```kotlinn@Service
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
