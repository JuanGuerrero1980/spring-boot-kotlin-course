# Clase 6 — Beans y Application Context

> **Fase:** 1 — Fundamentos de Spring
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Entender qué es un Bean.
* Entender qué es el ApplicationContext.
* Conocer `@Component`, `@Service`, `@Repository` y `@Controller`.
* Entender cómo Spring descubre y administra componentes.
* Entender qué es el scope Singleton.
* Entender por qué los Beans Singleton no deberían guardar estado mutable.
* Poder explicar estos conceptos en una entrevista técnica.

---

# 1. ¿Qué es un Bean?

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

# 2. `@Component`

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

# 3. `@Service`

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

# 4. `@Repository`

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

# 5. `@Controller`

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

`@RestController` combina `@Controller` y `@ResponseBody`.

---

# 6. Relación entre las anotaciones

```text
@Component
   │
   ├── @Service        → lógica de negocio
   ├── @Repository     → acceso a datos
   └── @Controller     → capa web / MVC
         │
         └── @RestController → APIs REST
```

Todas permiten registrar una clase como Bean, pero cada una expresa una intención diferente.

Usar la anotación correcta mejora la legibilidad del código.

---

# 7. ApplicationContext

El ApplicationContext es la representación principal del contenedor de Spring.

Cuando Spring arranca:

```text
Spring Boot inicia
      │
      ▼
ApplicationContext
      │
      ├── descubre componentes
      ├── registra Beans
      ├── resuelve dependencias
      └── inicia servicios
```

Podemos imaginar el ApplicationContext como un mapa de objetos administrados:

```text
ApplicationContext
       │
       ├── RechargeController
       ├── RechargeService
       ├── RechargeRepository
       ├── PaymentService
       └── NotificationService
```

---

# 8. Component Scanning

Spring necesita saber dónde buscar clases anotadas.

Por defecto, Spring Boot comienza a escanear desde el package donde se encuentra la clase principal con `@SpringBootApplication`.

En nuestro proyecto:

```text
com.payflow.api
       │
       ├── PayflowApplication
       │
       ├── controller/
       ├── service/
       └── repository/
```

Si respetamos esa estructura, Spring encontrará nuestros componentes automáticamente.

---

# 9. ¿Qué pasa si un Service necesita otro Service?

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

# 10. Scope Singleton

Por defecto, los Beans de Spring tienen:

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

# 11. Ciclo básico de un Bean

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

# 12. Modelo mental actualizado

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

* Controller: recibe requests HTTP.
* Service: contiene lógica de negocio.
* Repository: accede a datos.

Cada uno es un Bean administrado por Spring.

---

# Ejercicios

Resolvé estos ejercicios en tu cuaderno.

## Ejercicio 1

¿Qué es un Spring Bean?

## Ejercicio 2

¿Qué diferencia hay entre `@Component`, `@Service`, `@Repository` y `@Controller`?

## Ejercicio 3

Convertí estas clases para que Spring las administre como Beans, eligiendo la anotación correcta:

```kotlin
class UserRepository {
    fun findById(id: Long): User? { ... }
}

class UserService(
    private val userRepository: UserRepository
) {
    fun getUser(id: Long): User? { ... }
}

class UserController(
    private val userService: UserService
) {
    fun getUser(id: Long): UserResponse { ... }
}
```

## Ejercicio 4

¿Qué significa que un Bean tenga scope Singleton?

## Ejercicio 5

¿Por qué no deberíamos guardar estado mutable de una request en un Bean Singleton?

## Ejercicio 6

Describí el recorrido desde que Spring encuentra una clase anotada con `@Service` hasta que la inyecta en otro componente.

---

# Preguntas de entrevista

1. ¿Qué es un Spring Bean?
2. ¿Qué es el ApplicationContext?
3. ¿Qué diferencia hay entre `@Component`, `@Service`, `@Repository` y `@Controller`?
4. ¿Qué es component scanning?
5. ¿Qué significa que un Bean tenga scope Singleton?
6. ¿Por qué los Beans Singleton no deberían guardar estado mutable específico de una request?

---

# Resumen

```text
Bean
   │
   └── Objeto administrado por Spring

@Component
   │
   ├── @Service        → negocio
   ├── @Repository     → datos
   └── @Controller     → web

ApplicationContext
   │
   └── Contenedor que administra los Beans

Singleton scope
   │
   └── Una instancia reutilizada por toda la aplicación
```

---

# Checklist

* [ ] Entiendo qué es un Bean.
* [ ] Entiendo Spring Container.
* [ ] Entiendo ApplicationContext.
* [ ] Conozco `@Component`.
* [ ] Conozco `@Service`.
* [ ] Conozco `@Repository`.
* [ ] Conozco `@Controller`.
* [ ] Entiendo component scanning.
* [ ] Entiendo Singleton Scope.
* [ ] Entiendo por qué no guardar estado mutable en Beans Singleton.
* [ ] Completé los ejercicios.
* [ ] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 7 — Configuration y Profiles

Vamos a aprender cómo configurar nuestra aplicación y cómo manejar distintos entornos (desarrollo, testing, producción) con Spring Profiles.
