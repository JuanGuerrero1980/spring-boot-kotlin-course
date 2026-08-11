# Clase 3 — Spring Framework: IoC, Dependency Injection y Beans

> **Fase:** 1 — Fundamentos de Spring
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# 🎯 Objetivos

Al terminar esta clase deberías poder:

* Explicar qué problema resuelve Spring.
* Entender IoC — Inversion of Control.
* Entender Dependency Injection.
* Diferenciar Dependency Injection de simplemente crear objetos.
* Entender qué es un Bean.
* Entender qué es el Application Context.
* Comprender cómo Spring descubre y administra componentes.
* Conocer `@Component`, `@Service`, `@Repository` y `@Controller`.
* Entender constructor injection.
* Entender por qué preferimos constructor injection.
* Entender el concepto de Spring Container.
* Comprender el ciclo básico de creación de Beans.
* Entender qué sucede cuando Spring inicia una aplicación.
* Poder explicar estos conceptos en una entrevista técnica.

---

# 1. ¿Qué es Spring?

Spring es un framework para desarrollar aplicaciones Java y Kotlin.

Pero decir solamente:

> "Spring es un framework"

no explica demasiado.

Una de las ideas centrales de Spring es:

> **Spring se encarga de crear, configurar y administrar objetos de nuestra aplicación.**

Esto nos permite concentrarnos en la lógica de negocio.

Por ejemplo, imaginemos:

```text
RechargeController
       ↓
RechargeService
       ↓
RechargeRepository
```

Podríamos crear manualmente todos esos objetos:

```kotlin
val repository = RechargeRepository()
val service = RechargeService(repository)
val controller = RechargeController(service)
```

Pero a medida que nuestra aplicación crece, esto se vuelve difícil de mantener.

Spring se encarga de administrar estas dependencias.

---

# 2. El problema: crear objetos manualmente

Supongamos:

```kotlin
class RechargeRepository {

    fun save() {
        // guardar recarga
    }
}
```

Y nuestro Service:

```kotlin
class RechargeService(
    private val repository: RechargeRepository
) {

    fun recharge() {
        repository.save()
    }
}
```

Para utilizarlo manualmente:

```kotlin
val repository = RechargeRepository()

val service = RechargeService(
    repository
)
```

Esto funciona.

Pero imaginemos que nuestra aplicación crece:

```text
RechargeController
        ↓
RechargeService
        ↓
RechargeRepository
        ↓
Database
```

Además:

```text
PaymentService
        ↓
PaymentRepository
        ↓
Database
```

Y:

```text
NotificationService
        ↓
EmailService
        ↓
External API
```

La cantidad de objetos y dependencias empieza a crecer.

Si nosotros tenemos que crear y conectar manualmente todo:

```kotlin
val repository = ...
val service = ...
val notificationService = ...
val controller = ...
```

terminamos teniendo código encargado de crear otros objetos.

Eso se vuelve difícil de mantener.

---

# 3. ¿Qué pasa si cambia una dependencia?

Supongamos:

```kotlin
class RechargeService(
    private val repository: RechargeRepository
)
```

Más adelante decidimos agregar:

```kotlin
class NotificationService
```

Entonces:

```kotlin
class RechargeService(
    private val repository: RechargeRepository,
    private val notificationService: NotificationService
)
```

Ahora quien construye `RechargeService` necesita conocer también `NotificationService`.

Y si `NotificationService` tiene otra dependencia:

```kotlin
class NotificationService(
    private val emailClient: EmailClient
)
```

la cadena continúa:

```text
RechargeService
      │
      ├── RechargeRepository
      │
      └── NotificationService
                 │
                 └── EmailClient
```

¿Quién crea todo esto?

Spring.

---

# 4. Inversion of Control — IoC

IoC significa:

> **Inversion of Control**

En español:

> **Inversión de Control.**

La idea básica es:

> Nuestro código deja de ser completamente responsable de controlar la creación y administración de sus dependencias.

Sin Spring:

```text
Nuestro código
     │
     ├── crea Repository
     ├── crea Service
     ├── crea Controller
     └── conecta todo
```

Con Spring:

```text
Spring
  │
  ├── crea Repository
  ├── crea Service
  ├── crea Controller
  └── conecta dependencias
```

Nosotros definimos **qué necesita cada componente**.

Spring se ocupa de construir y conectar esos componentes.

Esto es IoC.

---

# 5. Dependency Injection

Dependency Injection significa:

> **Inyección de Dependencias.**

Supongamos:

```kotlin
class RechargeService(
    private val repository: RechargeRepository
)
```

`RechargeService` tiene una dependencia:

```text
RechargeRepository
```

En lugar de crearla internamente:

```kotlin
class RechargeService {

    private val repository = RechargeRepository()
}
```

la recibe desde afuera:

```kotlin
class RechargeService(
    private val repository: RechargeRepository
)
```

Eso es Dependency Injection.

---

# 6. IoC vs Dependency Injection

Es muy común confundir estos dos conceptos.

### IoC

Es el concepto general:

> El control de la creación y administración de objetos es delegado al framework o container.

### Dependency Injection

Es uno de los mecanismos utilizados para implementar IoC:

> Las dependencias son proporcionadas al objeto desde afuera.

Podemos visualizarlo:

```text
IoC
 │
 └── Dependency Injection
```

Por eso:

> **Dependency Injection es una forma de aplicar Inversion of Control.**

---

# 7. Una analogía sencilla

Imaginemos un restaurante.

Un chef necesita:

```text
cuchillo
ingredientes
horno
```

Podría construir su propio horno cada vez que necesita cocinar.

Sería absurdo.

Es mejor que alguien se encargue de preparar el entorno y entregarle lo necesario.

El chef simplemente dice:

> Necesito un horno y estos ingredientes.

En nuestro software:

```text
Service
   ↓
"Necesito Repository"
```

Spring responde:

```text
"Acá tenés tu Repository."
```

El Service no necesita saber cómo fue creado.

---

# 8. Constructor Injection

En Spring existen distintas formas de inyectar dependencias.

La que vamos a utilizar principalmente es:

> **Constructor Injection**

Por ejemplo:

```kotlin
class RechargeService(
    private val repository: RechargeRepository
)
```

La dependencia se recibe mediante el constructor.

---

# 9. ¿Por qué Constructor Injection?

## 9.1 Dependencias obligatorias

Si un Service necesita un Repository para funcionar:

```kotlin
class RechargeService(
    private val repository: RechargeRepository
)
```

es imposible crear correctamente el Service sin proporcionar el Repository.

Eso hace que el diseño sea explícito.

---

## 9.2 Inmutabilidad

Podemos utilizar:

```kotlin
private val repository
```

en lugar de:

```kotlin
private var repository
```

La dependencia no cambia durante la vida del objeto.

---

## 9.3 Testeabilidad

Podemos crear el Service manualmente en un test:

```kotlin
val repository = FakeRechargeRepository()

val service = RechargeService(
    repository
)
```

No necesitamos levantar todo Spring.

Esto es extremadamente importante.

---

## 9.4 Dependencias visibles

Al mirar:

```kotlin
class RechargeService(
    private val repository: RechargeRepository,
    private val notificationService: NotificationService
)
```

podemos ver inmediatamente de qué depende el Service.

No tenemos que buscar dependencias escondidas en distintas partes de la clase.

---

# 10. ¿Qué es un Bean?

Un **Bean** es un objeto que es administrado por Spring.

Por ejemplo:

```kotlin
@Service
class RechargeService
```

Spring detectará esa clase y creará una instancia administrada por el Spring Container.

Esa instancia es un Bean.

Podemos visualizarlo:

```text
Spring Container
       │
       ├── RechargeService Bean
       ├── RechargeRepository Bean
       ├── PaymentService Bean
       └── NotificationService Bean
```

Spring administra estos objetos.

---

# 11. Spring Container

El Spring Container es el componente encargado de administrar los Beans.

Conceptualmente:

```text
              Spring Container
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
   Controller     Service      Repository
```

El Container:

* crea Beans;
* configura Beans;
* conecta dependencias;
* administra su ciclo de vida.

---

# 12. ApplicationContext

Una de las implementaciones principales del Spring Container es:

```text
ApplicationContext
```

Podemos imaginarlo como:

> El contexto donde Spring mantiene y administra los Beans de nuestra aplicación.

Por ejemplo:

```text
ApplicationContext
       │
       ├── RechargeController
       ├── RechargeService
       ├── RechargeRepository
       ├── PaymentService
       └── NotificationService
```

Cuando Spring arranca nuestra aplicación, crea este contexto y registra los componentes correspondientes.

---

# 13. `@Component`

Una forma básica de indicarle a Spring:

> Esta clase debe ser administrada como Bean.

es:

```kotlin
@Component
class EmailService
```

Spring detectará esta clase durante el component scanning.

Entonces tendremos:

```text
EmailService
     ↓
Spring Container
     ↓
EmailService Bean
```

---

# 14. `@Service`

`@Service` es una especialización de `@Component`.

Se utiliza normalmente para representar componentes que contienen lógica de negocio.

Por ejemplo:

```kotlin
@Service
class RechargeService {

    fun recharge() {
        // lógica de negocio
    }
}
```

Conceptualmente:

```text
@Service
    ↓
@Component
    ↓
Spring Bean
```

Cuando vemos:

```kotlin
@Service
class RechargeService
```

sabemos:

> Esta clase pertenece a la capa de servicio/lógica de negocio.

---

# 15. `@Repository`

`@Repository` se utiliza para componentes relacionados con acceso a datos.

Por ejemplo:

```kotlin
@Repository
class RechargeRepository
```

Esto expresa:

> Esta clase pertenece a la capa de persistencia/acceso a datos.

Más adelante utilizaremos principalmente:

```text
Spring Data JPA
```

y muchas veces ni siquiera necesitaremos implementar manualmente el Repository.

---

# 16. `@Controller`

`@Controller` se utiliza para componentes que reciben solicitudes HTTP en aplicaciones Spring MVC.

Por ejemplo:

```kotlin
@Controller
class RechargeController
```

Más adelante veremos:

```kotlin
@RestController
```

que será especialmente importante para nuestras APIs REST.

---

# 17. Arquitectura básica

Nuestro backend tendrá aproximadamente:

```text
                 HTTP Request
                      │
                      ▼
               ┌─────────────┐
               │ Controller  │
               └──────┬──────┘
                      │
                      ▼
               ┌─────────────┐
               │   Service   │
               └──────┬──────┘
                      │
                      ▼
               ┌─────────────┐
               │ Repository  │
               └──────┬──────┘
                      │
                      ▼
                 PostgreSQL
```

Cada capa tiene una responsabilidad.

Esto conecta directamente con la Clase 1:

> Si ponemos todo en el Controller terminamos mezclando routing, HTTP y lógica de negocio.

Ahora Spring nos ayuda a construir estas capas y conectar sus dependencias.

---

# 18. Primer ejemplo completo

Imaginemos:

```kotlin
@Repository
class RechargeRepository {

    fun save() {
        println("Saving recharge")
    }
}
```

Nuestro Service:

```kotlin
@Service
class RechargeService(
    private val repository: RechargeRepository
) {

    fun recharge() {
        repository.save()
    }
}
```

Y nuestro Controller:

```kotlin
@RestController
class RechargeController(
    private val service: RechargeService
)
```

Tenemos:

```text
RechargeController
       │
       │ necesita
       ▼
RechargeService
       │
       │ necesita
       ▼
RechargeRepository
```

Spring puede construir la cadena:

```text
RechargeRepository
        ↓
RechargeService
        ↓
RechargeController
```

Nosotros no necesitamos escribir:

```kotlin
val repository = RechargeRepository()

val service = RechargeService(repository)

val controller = RechargeController(service)
```

Spring lo hace por nosotros.

---

# 19. ¿Cómo sabe Spring qué crear?

Spring realiza un proceso conocido como:

> **Component Scanning**

Busca clases que estén marcadas con componentes reconocibles por Spring.

Por ejemplo:

```text
@Component
@Service
@Repository
@Controller
@RestController
```

Cuando encuentra estas clases, puede registrarlas como Beans.

Conceptualmente:

```text
Spring inicia
     │
     ▼
Busca componentes
     │
     ▼
Encuentra @Service
     │
     ▼
Registra Bean
     │
     ▼
Analiza dependencias
     │
     ▼
Conecta Beans
```

---

# 20. ¿Qué pasa si un Service necesita otro Service?

Supongamos:

```kotlin
@Service
class NotificationService
```

Y:

```kotlin
@Service
class RechargeService(
    private val notificationService: NotificationService
)
```

Spring detecta:

```text
RechargeService
       │
       └── necesita NotificationService
```

Como `NotificationService` también es un Bean:

```text
Spring Container

NotificationService Bean
        │
        ▼
RechargeService Bean
```

Spring realiza la inyección.

---

# 21. ¿Qué pasa si falta una dependencia?

Supongamos:

```kotlin
@Service
class RechargeService(
    private val repository: RechargeRepository
)
```

pero `RechargeRepository` no está registrado como Bean.

Spring intentará crear `RechargeService`.

Pero descubrirá:

```text
Necesito RechargeRepository
        ↓
No existe Bean
        ↓
No puedo construir RechargeService
```

La aplicación fallará durante el arranque.

Esto es bueno porque Spring detecta el problema inmediatamente.

No esperamos a que un usuario haga una request para descubrir que falta una dependencia.

---

# 22. ¿Qué pasa si existen dos implementaciones?

Supongamos:

```kotlin
interface PaymentProcessor {

    fun process()
}
```

Tenemos:

```kotlin
@Component
class MercadoPagoProcessor : PaymentProcessor
```

y:

```kotlin
@Component
class StripeProcessor : PaymentProcessor
```

Ahora:

```kotlin
@Service
class PaymentService(
    private val processor: PaymentProcessor
)
```

Spring encuentra dos candidatos:

```text
PaymentProcessor
       │
       ├── MercadoPagoProcessor
       │
       └── StripeProcessor
```

Spring no sabe cuál elegir.

Más adelante aprenderemos:

```text
@Qualifier
@Primary
```

para resolver estos casos.

Por ahora debemos recordar:

> Spring necesita poder determinar qué Bean corresponde a cada dependencia.

---

# 23. Interfaces y Dependency Injection

Este concepto es muy importante para diseño de software.

Podemos tener:

```kotlin
interface PaymentProcessor {

    fun process()
}
```

Y varias implementaciones:

```kotlin
class MercadoPagoProcessor : PaymentProcessor
```

```kotlin
class StripeProcessor : PaymentProcessor
```

Nuestro Service puede depender de la abstracción:

```kotlin
class PaymentService(
    private val processor: PaymentProcessor
)
```

en lugar de depender directamente de:

```kotlin
MercadoPagoProcessor
```

Esto reduce el acoplamiento.

Conceptualmente:

```text
PaymentService
      ↓
PaymentProcessor
      ↑
      │
 ┌────┴────┐
 │         │
MercadoPago Stripe
```

Este diseño será muy importante cuando construyamos funcionalidades reales.

---

# 24. Dependency Injection y testing

Supongamos:

```kotlin
interface RechargeRepository {

    fun save()
}
```

Producción:

```kotlin
class PostgresRechargeRepository : RechargeRepository
```

Nuestro Service:

```kotlin
class RechargeService(
    private val repository: RechargeRepository
)
```

En producción:

```kotlin
val service = RechargeService(
    PostgresRechargeRepository()
)
```

Pero en un test podríamos usar:

```kotlin
class FakeRechargeRepository : RechargeRepository {

    override fun save() {
        // fake
    }
}
```

Y:

```kotlin
val service = RechargeService(
    FakeRechargeRepository()
)
```

El Service no necesita saber qué implementación recibió.

Esto es una de las grandes ventajas de Dependency Injection.

---

# 25. Constructor Injection en Kotlin + Spring

En Java tradicional podemos encontrarnos con:

```java
@Autowired
private RechargeService service;
```

No queremos aprender Spring de esa forma.

En Kotlin preferiremos:

```kotlin
@RestController
class RechargeController(
    private val service: RechargeService
)
```

Spring detecta el constructor y realiza la inyección.

No necesitamos escribir:

```kotlin
@Autowired
```

cuando existe un único constructor.

Esto produce código más limpio.

---

# 26. ¿Qué hace realmente Spring?

Sin Spring:

```text
Nuestro código
     │
     ├── crea objetos
     ├── busca dependencias
     ├── conecta objetos
     └── administra su ciclo de vida
```

Con Spring:

```text
Spring Container
     │
     ├── crea objetos
     ├── encuentra dependencias
     ├── conecta objetos
     └── administra Beans
```

Nuestro código se concentra principalmente en:

```text
reglas de negocio
```

y no en:

```text
cómo construir todo el grafo de objetos
```

---

# 27. Dependency Graph

Cuando una aplicación crece, podemos pensar en sus componentes como un grafo de dependencias.

Por ejemplo:

```text
                    Controller
                        │
                        ▼
                 RechargeService
                  /            \
                 ▼              ▼
        RechargeRepository   NotificationService
                 │                    │
                 ▼                    ▼
             PostgreSQL          EmailClient
```

Spring construye y administra este grafo.

Esto se conoce como:

> **Dependency Graph**

Es un concepto importante para entender cómo funciona Spring.

---

# 28. ¿Spring crea una instancia por cada request?

Por defecto, no.

Los Beans de Spring normalmente tienen:

> **Singleton scope**

Esto significa que Spring normalmente crea una instancia del Bean y la reutiliza.

Por ejemplo:

```text
RechargeService
       │
       ▼
   instancia 1
       │
       ├── Request 1
       ├── Request 2
       ├── Request 3
       └── Request 4
```

Esto tiene una consecuencia importante:

> Los Services y otros Beans singleton deben diseñarse pensando en concurrencia.

Debemos evitar almacenar estado mutable específico de una request:

```kotlin
@Service
class RechargeService {

    private var currentUser: String? = null
}
```

Esto sería peligroso.

Distintas requests podrían acceder al mismo Bean.

Preferimos:

```kotlin
@Service
class RechargeService {

    fun recharge(userId: Long) {
        // datos propios de esta operación
    }
}
```

El estado de la operación debe estar en variables locales o estructuras apropiadas, no en propiedades mutables compartidas del Singleton.

---

# 29. Ciclo básico de un Bean

Simplificando mucho, cuando Spring inicia:

```text
1. Spring arranca
       ↓
2. Encuentra componentes
       ↓
3. Registra Bean definitions
       ↓
4. Resuelve dependencias
       ↓
5. Crea Beans
       ↓
6. Inyecta dependencias
       ↓
7. Inicializa Beans
       ↓
8. ApplicationContext queda listo
```

No necesitamos memorizar todos los detalles internos todavía.

Lo importante es entender el concepto.

---

# 30. Primer modelo mental de Spring

Quiero que tengas este modelo mental:

```text
                    SPRING
                      │
                      ▼
              ApplicationContext
                      │
             ┌────────┼────────┐
             │        │        │
             ▼        ▼        ▼
        Controller  Service  Repository
             │        │        │
             └────────┼────────┘
                      │
                      ▼
                Dependencies
```

Spring administra estos objetos.

Nosotros definimos sus responsabilidades y dependencias.

---

# 🧪 Ejercicios

## Ejercicio 1 — Sin Spring

Tenemos:

```kotlin
class PaymentRepository {

    fun save() {
        println("Payment saved")
    }
}

class PaymentService(
    private val repository: PaymentRepository
) {

    fun pay() {
        repository.save()
    }
}
```

### Respuesta

```kotlin
val paymentService = PaymentService(
    PaymentRepository()
)
```

Acá estamos realizando manualmente la Dependency Injection.

---

## Ejercicio 2 — Identificar Dependency Injection

Tenemos:

```kotlin
class PaymentService(
    private val repository: PaymentRepository
)
```

### Respuesta

La dependencia de `PaymentService` es `PaymentRepository`.

La dependencia se proporciona desde afuera y se inyecta mediante el constructor.

Esto es **constructor injection**.

---

## Ejercicio 3 — Convertirlo en Spring

Tenemos:

```kotlin
class PaymentRepository {

    fun save() {
        println("Payment saved")
    }
}

class PaymentService(
    private val repository: PaymentRepository
) {

    fun pay() {
        repository.save()
    }
}
```

### Respuesta

```kotlin
@Repository
class PaymentRepository {
    // ...
}
```

y:

```kotlin
@Service
class PaymentService(
    private val repository: PaymentRepository
) {
    // ...
}
```

Spring podrá administrar ambas clases como Beans.

---

## Ejercicio 4 — Arquitectura

Tenemos:

```text
PaymentController
PaymentService
PaymentRepository
```

### Respuesta

Como relación de dependencias:

```text
PaymentController
       ↓
PaymentService
       ↓
PaymentRepository
```

Esto significa:

```text
PaymentController DEPENDE de PaymentService

PaymentService DEPENDE de PaymentRepository
```

Cada capa mantiene una responsabilidad diferente.

---

## Ejercicio 5 — Problema de diseño

Tenemos:

```kotlin
@Service
class PaymentService {

    private val repository = PaymentRepository()

    fun pay() {
        repository.save()
    }
}
```

### Respuesta

El problema es que `PaymentService` está creando directamente su propia dependencia.

Esto:

* aumenta el acoplamiento;
* dificulta los tests;
* evita que Spring controle esa dependencia;
* hace más difícil reemplazar la implementación.

Preferimos:

```kotlin
@Service
class PaymentService(
    private val repository: PaymentRepository
) {

    fun pay() {
        repository.save()
    }
}
```

Ahora la dependencia es proporcionada desde afuera mediante el constructor.

---

## Ejercicio 6 — Interfaces

Tenemos:

```kotlin
interface PaymentProcessor {

    fun process()
}
```

y:

```kotlin
@Component
class MercadoPagoProcessor : PaymentProcessor {

    override fun process() {
        println("Mercado Pago")
    }
}
```

### Respuesta

Es mejor que `PaymentService` dependa de:

```kotlin
PaymentProcessor
```

porque `PaymentProcessor` representa la abstracción.

`MercadoPagoProcessor` es solamente una implementación concreta.

Esto permite reemplazar la implementación sin modificar el Service.

---

## Ejercicio 7 — Dos Beans

Tenemos:

```kotlin
@Component
class MercadoPagoProcessor : PaymentProcessor
```

y:

```kotlin
@Component
class StripeProcessor : PaymentProcessor
```

Y:

```kotlin
@Service
class PaymentService(
    private val processor: PaymentProcessor
)
```

### Respuesta

Spring encuentra dos Beans compatibles con `PaymentProcessor`:

```text
PaymentProcessor
       │
       ├── MercadoPagoProcessor
       └── StripeProcessor
```

Por lo tanto, Spring no sabe cuál debe inyectar.

Más adelante podremos resolverlo mediante:

```text
@Primary
@Qualifier
```

---

# 🎤 Preguntas de entrevista

### Spring

> ¿Qué es IoC?

**Respuesta esperada:**

IoC es la inversión de control. En lugar de que nuestro código sea responsable de crear y administrar sus dependencias, delegamos esa responsabilidad a un container como el de Spring.

---

### Spring

> ¿Qué es Dependency Injection?

**Respuesta esperada:**

Es un mecanismo mediante el cual un objeto recibe desde afuera las dependencias que necesita, en lugar de crearlas internamente.

---

### Spring

> ¿Cuál es la diferencia entre IoC y Dependency Injection?

**Respuesta esperada:**

IoC es el concepto general de delegar el control de creación y administración de objetos. Dependency Injection es uno de los mecanismos utilizados para implementar esa inversión de control.

---

### Spring

> ¿Qué es un Spring Bean?

**Respuesta esperada:**

Es un objeto cuya creación y ciclo de vida son administrados por el Spring Container.

---

### Spring

> ¿Qué es el ApplicationContext?

**Respuesta esperada:**

Es la representación principal del contexto de Spring que contiene y administra los Beans y sus dependencias.

---

### Spring

> ¿Qué diferencia hay entre `@Component`, `@Service` y `@Repository`?

**Respuesta esperada:**

Las tres permiten registrar componentes como Beans, pero expresan diferentes responsabilidades. `@Component` es genérico, `@Service` representa normalmente lógica de negocio y `@Repository` representa acceso a datos.

---

### Spring

> ¿Qué es constructor injection?

**Respuesta esperada:**

Es la inyección de dependencias mediante el constructor de una clase.

---

### Spring

> ¿Por qué preferís constructor injection?

**Respuesta esperada:**

Porque hace explícitas las dependencias, permite utilizar `val`, facilita la inmutabilidad y mejora la testeabilidad.

---

### Spring

> ¿Por qué Dependency Injection mejora la testeabilidad?

**Respuesta esperada:**

Porque podemos proporcionar implementaciones alternativas, como mocks o fakes, sin modificar la clase que estamos testeando.

---

### Spring

> ¿Qué sucede si Spring encuentra dos Beans compatibles con una misma dependencia?

**Respuesta esperada:**

Spring no puede determinar automáticamente cuál utilizar y debemos resolver la ambigüedad, por ejemplo mediante `@Primary` o `@Qualifier`.

---

### Spring

> ¿Qué significa que un Bean tenga scope Singleton?

**Respuesta esperada:**

Significa que, por defecto, Spring administra una única instancia del Bean dentro del ApplicationContext y la reutiliza para las distintas requests.

---

# 🧠 Lo que debo poder explicar sin mirar

Al terminar esta clase debería poder explicar:

* Qué problema resuelve Spring.
* Qué significa IoC.
* Qué significa Dependency Injection.
* La diferencia entre IoC y DI.
* Qué es un Bean.
* Qué es el Spring Container.
* Qué es el ApplicationContext.
* Qué hace `@Component`.
* Qué representa `@Service`.
* Qué representa `@Repository`.
* Qué representa `@Controller`.
* Qué es constructor injection.
* Por qué constructor injection es preferible.
* Cómo Spring descubre componentes.
* Cómo Spring resuelve dependencias.
* Qué ocurre si falta una dependencia.
* Qué ocurre si existen dos Beans compatibles.
* Qué es un Dependency Graph.
* Qué significa Singleton Scope.
* Por qué los Beans Singleton no deberían guardar estado mutable específico de una request.
* Cómo Dependency Injection facilita los tests.

---

# ☑️ Checklist

* [x] Entiendo qué problema resuelve Spring.
* [x] Entiendo IoC.
* [x] Entiendo Dependency Injection.
* [x] Puedo explicar IoC vs DI.
* [x] Entiendo qué es un Bean.
* [x] Entiendo Spring Container.
* [x] Entiendo ApplicationContext.
* [x] Conozco `@Component`.
* [x] Conozco `@Service`.
* [x] Conozco `@Repository`.
* [x] Conozco `@Controller`.
* [x] Entiendo constructor injection.
* [x] Sé por qué preferimos constructor injection.
* [x] Entiendo component scanning.
* [x] Entiendo dependency graph.
* [x] Entiendo qué pasa cuando falta una dependencia.
* [x] Entiendo qué pasa cuando existen múltiples implementaciones.
* [x] Entiendo Singleton Scope.
* [x] Completé los ejercicios.
* [x] Respondí las preguntas de entrevista.

---

# 🏁 Estado de la clase

**🟢 COMPLETADA**

Los ejercicios fueron revisados.

Resultado:

> **7/7 ejercicios correctos conceptualmente.**

El único ajuste realizado fue en el ejercicio 4, donde la dirección correcta de las dependencias es:

```text
PaymentController
       ↓
PaymentService
       ↓
PaymentRepository
```

Recordar:

> **A depende de B → A necesita a B.**

---

# 🚀 Próxima clase

# Clase 4 — Crear nuestro primer proyecto Spring Boot con Kotlin

En la próxima clase pasaremos de la teoría a código real.

Crearemos:

```text
PayFlow API
│
├── Spring Boot
├── Kotlin
├── Gradle
└── Java
```

Y veremos:

```text
Spring Initializr
        ↓
Proyecto
        ↓
Gradle
        ↓
Application
        ↓
Spring Boot
        ↓
Primer endpoint
        ↓
Primera request HTTP
```

También vamos a entender realmente qué significa:

```kotlin
@SpringBootApplication
```

y qué sucede cuando ejecutamos:

```text
./gradlew bootRun
```

A partir de esta clase comenzaremos a construir **PayFlow API de manera incremental**, utilizando cada concepto del curso en un proyecto real.
