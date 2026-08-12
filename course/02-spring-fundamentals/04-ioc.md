# Clase 4 — IoC: Inversion of Control

> **Fase:** 1 — Fundamentos de Spring
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Explicar qué problema resuelve Spring.
* Entender qué es Inversion of Control.
* Diferenciar entre crear objetos manualmente y delegar en Spring.
* Entender el concepto de Spring Container.
* Comprender qué sucede cuando Spring inicia una aplicación.
* Poder explicar IoC en una entrevista técnica.

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

# 5. Una analogía sencilla

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

# 6. IoC no es solo Spring

Inversion of Control es un principio general.

Spring es una de las implementaciones más conocidas, pero el concepto también aparece en otros lados:

* frameworks que inyectan dependencias;
* contenedores que gestionan el ciclo de vida de objetos;
* bibliotecas que invocan nuestro código en lugar de nosotros invocarlas.

Lo importante es entender el principio:

> **Delegamos el control de ciertas responsabilidades a un framework o componente externo.**

---

# 7. Spring Container

El Spring Container es el componente encargado de administrar los objetos que Spring crea.

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

* crea objetos;
* configura objetos;
* conecta dependencias;
* administra su ciclo de vida.

A estos objetos administrados por el Container los llamamos **Beans**.

Lo veremos con más detalle en la próxima clase.

---

# 8. ApplicationContext

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

# 9. ¿Qué hace realmente Spring?

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

# 10. ¿Cómo sabe Spring qué crear?

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

# 11. Ciclo básico de arranque

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

# 12. Primer modelo mental de Spring

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

# Ejercicios

Resolvé estos ejercicios en tu cuaderno. Las respuestas las revisaremos juntos.

## Ejercicio 1

¿Qué problema resuelve Spring cuando nuestra aplicación crece?

## Ejercicio 2

Explicá con tus palabras qué significa **Inversion of Control**.

## Ejercicio 3

¿Cuál es la diferencia entre crear objetos manualmente y dejar que Spring los administre?

## Ejercicio 4

¿Qué papel cumple el **Spring Container**?

## Ejercicio 5

¿Qué es el **ApplicationContext**?

## Ejercicio 6

Describí el ciclo de arranque de Spring en tus propias palabras.

---

# Preguntas de entrevista

1. ¿Qué es Inversion of Control?
2. ¿Por qué es útil delegar la creación de objetos a un framework como Spring?
3. ¿Qué es el Spring Container?
4. ¿Qué es el ApplicationContext?
5. ¿Qué es el component scanning?

---

# Resumen

```text
Sin Spring
   │
   └── Nuestro código crea y conecta todo

Con Spring
   │
   └── Spring Container crea y conecta los objetos

IoC
   │
   └── Invertimos el control de la creación de objetos

ApplicationContext
   │
   └── Es donde Spring mantiene y administra los Beans
```

---

# Checklist

* [ ] Entiendo qué problema resuelve Spring.
* [ ] Entiendo IoC.
* [ ] Puedo explicar IoC con mis palabras.
* [ ] Entiendo Spring Container.
* [ ] Entiendo ApplicationContext.
* [ ] Entiendo component scanning.
* [ ] Entiendo el ciclo básico de arranque de Spring.
* [ ] Completé los ejercicios.
* [ ] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 5 — Dependency Injection

Vamos a profundizar en el mecanismo principal que utiliza Spring para implementar IoC:

```text
Dependency Injection
```

Veremos qué es, por qué preferimos constructor injection y cómo se aplica en Kotlin.
