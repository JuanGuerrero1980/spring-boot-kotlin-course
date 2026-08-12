# Clase 5 — Dependency Injection

> **Fase:** 1 — Fundamentos de Spring
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Entender qué es Dependency Injection.
* Diferenciar Dependency Injection de simplemente crear objetos.
* Entender constructor injection.
* Entender por qué preferimos constructor injection.
* Entender cómo Spring resuelve dependencias.
* Entender qué ocurre si falta una dependencia.
* Entender qué ocurre si existen dos implementaciones.
* Comprender cómo DI facilita los tests.
* Poder explicar estos conceptos en una entrevista técnica.

---

# 1. Dependency Injection

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

# 2. IoC vs Dependency Injection

Es muy común confundir estos dos conceptos.

## IoC

Es el concepto general:

> El control de la creación y administración de objetos es delegado al framework o container.

## Dependency Injection

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

# 3. Constructor Injection

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

# 4. ¿Por qué Constructor Injection?

## 4.1 Dependencias obligatorias

Si un Service necesita un Repository para funcionar:

```kotlin
class RechargeService(
    private val repository: RechargeRepository
)
```

es imposible crear correctamente el Service sin proporcionar el Repository.

Eso hace que el diseño sea explícito.

---

## 4.2 Inmutabilidad

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

## 4.3 Testeabilidad

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

## 4.4 Dependencias visibles

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

# 5. Constructor Injection en Kotlin + Spring

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

# 6. ¿Qué pasa si falta una dependencia?

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

# 7. ¿Qué pasa si existen dos implementaciones?

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

# 8. Interfaces y Dependency Injection

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

# 9. Dependency Injection y testing

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

# 10. Dependency Graph

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

# Ejercicios

Resolvé estos ejercicios en tu cuaderno.

## Ejercicio 1

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

¿Cómo crearías manualmente un `PaymentService`? ¿Dónde ocurre la Dependency Injection?

## Ejercicio 2

Identificá la dependencia en este código:

```kotlin
class PaymentService(
    private val repository: PaymentRepository
)
```

¿Cómo se llama el mecanismo de inyección utilizado?

## Ejercicio 3

Convertí el siguiente código para que Spring pueda administrar las clases como Beans:

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

## Ejercicio 4

¿Qué problema tiene este diseño?

```kotlin
@Service
class PaymentService {

    private val repository = PaymentRepository()

    fun pay() {
        repository.save()
    }
}
```

## Ejercicio 5

¿Por qué es mejor que `PaymentService` dependa de una interfaz `PaymentProcessor` en lugar de depender directamente de `MercadoPagoProcessor`?

## Ejercicio 6

Tenemos:

```kotlin
@Component
class MercadoPagoProcessor : PaymentProcessor

@Component
class StripeProcessor : PaymentProcessor

@Service
class PaymentService(
    private val processor: PaymentProcessor
)
```

¿Qué problema puede ocurrir al arrancar Spring? ¿Cómo se resuelve?

---

# Preguntas de entrevista

1. ¿Qué es Dependency Injection?
2. ¿Cuál es la diferencia entre IoC y Dependency Injection?
3. ¿Qué es constructor injection?
4. ¿Por qué preferís constructor injection?
5. ¿Por qué Dependency Injection mejora la testeabilidad?
6. ¿Qué sucede si Spring encuentra dos Beans compatibles con una misma dependencia?
7. ¿Por qué es preferible depender de abstracciones (interfaces) en lugar de implementaciones concretas?

---

# Resumen

```text
Dependency Injection
   │
   └── Las dependencias se reciben desde afuera

Constructor Injection
   │
   └── Las dependencias entran por el constructor

Ventajas
   │
   ├── Dependencias explícitas
   ├── Inmutabilidad (val)
   ├── Testeabilidad
   └── Menor acoplamiento

Problemas que Spring detecta al arrancar
   │
   ├── Falta una dependencia
   └── Existen múltiples implementaciones
```

---

# Checklist

* [ ] Entiendo Dependency Injection.
* [ ] Puedo explicar IoC vs DI.
* [ ] Entiendo constructor injection.
* [ ] Sé por qué preferimos constructor injection.
* [ ] Entiendo cómo Spring resuelve dependencias.
* [ ] Entiendo qué pasa cuando falta una dependencia.
* [ ] Entiendo qué pasa cuando existen múltiples implementaciones.
* [ ] Entiendo cómo DI facilita los tests.
* [ ] Entiendo el concepto de depender de abstracciones.
* [ ] Completé los ejercicios.
* [ ] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 6 — Beans y Application Context

Vamos a profundizar en qué es un Bean, cómo Spring lo administra y qué relación tiene con el ApplicationContext.
