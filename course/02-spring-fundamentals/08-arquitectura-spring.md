# Clase 8 — Arquitectura de una aplicación Spring

> **Fase:** 1 — Fundamentos de Spring
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Entender la arquitectura en capas de una aplicación Spring.
* Diferenciar las responsabilidades de Controller, Service y Repository.
* Entender por qué no colocamos toda la lógica en un Controller.
* Comprender el flujo completo de una request HTTP.
* Entender cómo se conectan IoC, DI y Beans con la arquitectura.
* Tener un modelo mental sólido antes de construir endpoints reales.

---

# 1. Arquitectura en capas

Una arquitectura común para aplicaciones Spring es:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

Cada capa tiene una responsabilidad clara.

Esta separación nos permite:

* mantener el código organizado;
* testear cada capa de forma independiente;
* reutilizar lógica de negocio;
* cambiar implementaciones sin afectar al resto.

---

# 2. Controller

El Controller se ocupa principalmente de la comunicación HTTP.

```text
Request
   ↓
Controller
   ↓
Response
```

Sus responsabilidades son:

* recibir requests;
* interpretar parámetros, headers y body;
* delegar al Service;
* devolver responses con el código HTTP adecuado.

No debería contener lógica de negocio compleja.

Ejemplo:

```kotlin
@RestController
@RequestMapping("/api/recharges")
class RechargeController(
    private val rechargeService: RechargeService
) {

    @PostMapping
    fun create(@RequestBody request: CreateRechargeRequest): RechargeResponse {
        return rechargeService.create(request)
    }
}
```

---

# 3. Service

El Service contiene la lógica de negocio.

```text
RechargeService
```

Podría encargarse de:

* comprobar que la cuenta está activa;
* validar límites;
* comprobar disponibilidad;
* ejecutar la operación;
* coordinar diferentes repositorios;
* aplicar una transacción.

Ejemplo:

```kotlin
@Service
class RechargeService(
    private val rechargeRepository: RechargeRepository
) {

    fun create(request: CreateRechargeRequest): RechargeResponse {
        // reglas de negocio
        // validaciones
        // persistencia
    }
}
```

---

# 4. Repository

El Repository se ocupa del acceso a los datos.

```text
Service
   ↓
Repository
   ↓
Database
```

Más adelante utilizaremos:

```text
Spring Data JPA
```

para simplificar gran parte de este trabajo.

Por ahora podemos imaginar un Repository como la capa que guarda y recupera información.

Ejemplo:

```kotlin
@Repository
class RechargeRepository {

    private val storage = mutableMapOf<Long, Recharge>()

    fun save(recharge: Recharge): Recharge {
        storage[recharge.id] = recharge
        return recharge
    }

    fun findById(id: Long): Recharge? {
        return storage[id]
    }
}
```

---

# 5. ¿Por qué separar las capas?

Podríamos poner todo dentro de un Controller:

```kotlin
@RestController
class AccountController {

    // HTTP
    // validación
    // lógica de negocio
    // acceso a base de datos
    // transacciones
    // respuesta
}
```

Al principio puede parecer sencillo.

Pero a medida que el sistema crece, el Controller se convertiría en una clase enorme y difícil de mantener.

Además, estaríamos mezclando responsabilidades:

```text
HTTP
+
Business Logic
+
Data Access
```

Esto genera varios problemas:

* clases demasiado grandes;
* dificultad para leer y mantener el código;
* lógica difícil de reutilizar;
* mayor acoplamiento;
* tests más complejos;
* dificultad para modificar una parte sin afectar otras;
* responsabilidades poco claras.

Separando las capas:

```text
Controller
    ↓
Service
    ↓
Repository
```

podemos mantener responsabilidades más claras.

También podemos probar cada parte de manera más independiente:

```text
Controller
    → tests HTTP

Service
    → tests de lógica de negocio

Repository
    → tests de persistencia
```

> **Importante:** la separación en capas no significa que haya que crear clases innecesariamente. La arquitectura debe ayudar al proyecto, no convertirse en burocracia.

---

# 6. Flujo completo de una request

Imaginemos que un cliente quiere crear una recarga:

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

El flujo sería:

```text
Cliente
   │
   │ HTTP POST /api/recharges
   ▼
RechargeController
   │
   │ @RequestBody
   ▼
CreateRechargeRequest
   │
   ▼
RechargeService
   │
   ├── valida reglas de negocio
   ├── usa RechargeRepository
   ▼
RechargeRepository
   │
   ▼
Storage / Database
   │
   ▼
RechargeResponse
   │
   ▼
HTTP Response 201 Created
   │
   ▼
Cliente
```

Este será uno de los flujos principales de nuestro proyecto.

---

# 7. DTOs en cada capa

En la frontera de la API usamos DTOs:

```kotlin
data class CreateRechargeRequest(
    val amount: BigDecimal,
    val phoneNumber: String,
    val operator: Operator,
    val type: RechargeType
)
```

```kotlin
data class RechargeResponse(
    val id: Long,
    val amount: BigDecimal,
    val status: RechargeStatus
)
```

El Controller recibe y devuelve DTOs.

El Service puede trabajar con DTOs o con objetos de dominio.

El Repository se encarga de persistir.

No exponemos entidades JPA directamente como respuestas HTTP.

---

# 8. Inyección de dependencias en la arquitectura

Spring conecta las capas mediante DI:

```kotlin
@RestController
class RechargeController(
    private val rechargeService: RechargeService
)
```

```kotlin
@Service
class RechargeService(
    private val rechargeRepository: RechargeRepository
)
```

```kotlin
@Repository
class RechargeRepository
```

Spring crea el grafo:

```text
RechargeRepository
        ↓
RechargeService
        ↓
RechargeController
```

Nosotros no escribimos:

```kotlin
val repository = RechargeRepository()
val service = RechargeService(repository)
val controller = RechargeController(service)
```

Spring lo hace por nosotros.

---

# 9. Arquitectura inicial de PayFlow

Nuestro objetivo será llegar progresivamente a algo similar a:

```text
                         CLIENTES
                            │
                ┌───────────┼───────────┐
                │           │           │
             Android       Web      Other API
                │           │           │
                └───────────┼───────────┘
                            │
                           HTTP
                            │
                            ▼
                  ┌─────────────────┐
                  │   Spring Boot   │
                  │                 │
                  │   Controllers   │
                  │        ↓        │
                  │     Services    │
                  │        ↓        │
                  │   Repositories  │
                  └────────┬────────┘
                           │
                          JPA
                           │
                           ▼
                  ┌─────────────────┐
                  │   PostgreSQL    │
                  └─────────────────┘
```

Más adelante agregaremos componentes como:

```text
Spring Security
JWT
Redis
Docker
Testcontainers
GitHub Actions
```

---

# Ejercicios

Resolvé estos ejercicios en tu cuaderno.

## Ejercicio 1

¿Qué responsabilidad tiene cada capa en una arquitectura Controller → Service → Repository?

## Ejercicio 2

¿Por qué no deberíamos colocar toda la lógica de negocio dentro de un Controller?

## Ejercicio 3

Dibujá el flujo completo de una request `POST /api/recharges` desde el cliente hasta la base de datos y de regreso.

## Ejercicio 4

¿Cuál es la ventaja de que `RechargeService` reciba `RechargeRepository` por constructor en lugar de crearlo dentro?

## Ejercicio 5

¿Por qué no exponemos entidades JPA directamente como respuestas HTTP?

## Ejercicio 6

Diseñá las tres capas para un endpoint `GET /api/recharges/{id}`. Indicá qué recibe y qué devuelve cada capa.

---

# Preguntas de entrevista

1. ¿Por qué separar Controller, Service y Repository?
2. ¿Qué responsabilidad tiene un Controller?
3. ¿Qué responsabilidad tiene un Service?
4. ¿Qué responsabilidad tiene un Repository?
5. ¿Cuál es el problema de poner lógica de negocio en el Controller?
6. ¿Cómo se conectan las capas en una aplicación Spring?
7. ¿Por qué usamos DTOs en lugar de exponer entidades directamente?

---

# Resumen

```text
Controller
   │
   └── HTTP: recibir request, delegar, devolver response

Service
   │
   └── Lógica de negocio

Repository
   │
   └── Acceso a datos

Spring Container
   │
   └── Crea e inyecta las dependencias entre capas
```

---

# Checklist

* [x] Entiendo la arquitectura en capas.
* [x] Sé qué responsabilidad tiene cada capa.
* [x] Entiendo por qué separar Controller, Service y Repository.
* [x] Puedo dibujar el flujo completo de una request.
* [x] Entiendo el papel de los DTOs.
* [x] Entiendo cómo Spring conecta las capas mediante DI.
* [x] Completé los ejercicios.
* [x] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 9 — Controllers

Empezamos la Fase 2: REST API.

Vamos a construir nuestros primeros controllers reales en PayFlow API, trabajando con DTOs, JSON y HTTP.
