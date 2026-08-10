# Clase 2 — Kotlin para Backend

> **Fase:** 0 — Preparación
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

## 🎯 Objetivos

Al terminar esta clase deberías poder:

* Utilizar `data class` correctamente para DTOs.
* Entender null safety aplicado a APIs.
* Diferenciar `class`, `data class`, `object`, `enum class` y `sealed class`.
* Utilizar collections de Kotlin de manera idiomática.
* Entender cuándo utilizar `BigDecimal` para dinero.
* Manejar excepciones correctamente.
* Entender extension functions.
* Comprender las scope functions sin abusar de ellas.
* Entender `suspend` y el papel de coroutines en backend.
* Conocer algunas particularidades de Kotlin + Spring/JPA.
* Diferenciar DTOs de Entities.

---

# 1. Kotlin en Backend

Si ya tenés experiencia con Android y Kotlin, muchas características del lenguaje te van a resultar conocidas.

Vamos a seguir utilizando:

```text
val
var
data class
sealed class
enum class
null safety
collections
extensions
lambdas
coroutines
```

Pero el contexto cambia.

En Android normalmente podemos pensar en:

```text
UI
 ↓
ViewModel
 ↓
Repository
 ↓
API
```

En backend vamos a pensar en:

```text
HTTP
 ↓
Controller
 ↓
Service
 ↓
Repository
 ↓
Database
```

Kotlin será el lenguaje que utilizaremos para implementar estas capas.

---

# 2. `val` vs `var`

La recomendación general en Kotlin es:

> **Preferir `val` siempre que sea posible.**

Por ejemplo:

```kotlin
val name = "Juan"
```

Una vez asignado, no podemos reasignar la variable.

En cambio:

```kotlin
var name = "Juan"

name = "Pedro"
```

permite reasignación.

En backend, favorecer la inmutabilidad ayuda a que el código sea más predecible y fácil de razonar.

Por ejemplo:

```kotlin
data class CreateUserRequest(
    val name: String,
    val email: String
)
```

es un buen modelo para representar los datos de entrada de una API.

---

# 3. `data class`

`data class` será extremadamente importante en nuestro backend.

Un DTO podría ser:

```kotlin
data class CreateUserRequest(
    val name: String,
    val email: String
)
```

Y otro para la respuesta:

```kotlin
data class UserResponse(
    val id: Long,
    val name: String,
    val email: String
)
```

Una `data class` proporciona automáticamente funcionalidades como:

* `equals()`
* `hashCode()`
* `toString()`
* `copy()`

Por ejemplo:

```kotlin
val user = UserResponse(
    id = 1,
    name = "Juan",
    email = "juan@example.com"
)

val updatedUser = user.copy(
    name = "Pedro"
)
```

Esto es especialmente útil para objetos que representan **datos**.

---

# 4. DTO

DTO significa:

**Data Transfer Object**

Un DTO representa información que entra o sale de nuestro sistema.

Por ejemplo:

```kotlin
data class CreateRechargeRequest(
    val amount: BigDecimal,
    val productId: Long
)
```

Y:

```kotlin
data class RechargeResponse(
    val id: Long,
    val amount: BigDecimal,
    val status: RechargeStatus
)
```

No necesariamente queremos exponer directamente nuestras entidades de base de datos.

Por eso normalmente tendremos:

```text
HTTP
 ↓
Request DTO
 ↓
Service
 ↓
Entity
 ↓
Database
```

Y en sentido contrario:

```text
Database
 ↓
Entity
 ↓
Service
 ↓
Response DTO
 ↓
HTTP
```

Esto será muy importante cuando lleguemos a Spring Data JPA.

---

# 5. DTO vs Entity

Es importante diferenciar ambos conceptos.

## DTO

Representa datos que entran o salen de nuestra API.

```kotlin
data class CreatePaymentRequest(
    val amount: BigDecimal,
    val type: PaymentType
)
```

## Entity

Representa un objeto persistido y administrado por JPA/Hibernate.

Por ejemplo, eventualmente tendremos algo parecido a:

```kotlin
@Entity
class Payment(
    // ...
)
```

No debemos asumir que:

```text
DTO = Entity
```

Aunque puedan tener algunos campos similares, tienen responsabilidades diferentes.

El DTO pertenece principalmente a la frontera de nuestra API.

La Entity pertenece principalmente a la capa de persistencia.

Esto nos permite evitar acoplar directamente nuestra API a la estructura de la base de datos.

---

# 6. Null Safety

Una de las características más importantes de Kotlin es su sistema de null safety.

Tenemos:

```kotlin
val name: String = "Juan"
```

`name` no puede ser `null`.

En cambio:

```kotlin
val name: String? = null
```

puede ser `null`.

Esto nos obliga a tratar explícitamente el caso.

Por ejemplo:

```kotlin
val length = name?.length
```

Si `name` es `null`, el resultado será `null`.

También podemos utilizar:

```kotlin
val length = name?.length ?: 0
```

Aquí:

* si `name` no es `null` → devuelve `length`;
* si `name` es `null` → devuelve `0`.

---

# 7. Null Safety en APIs

Esto se vuelve especialmente importante en backend.

Supongamos que recibimos:

```json
{
  "name": "Juan",
  "phone": null
}
```

Nuestro DTO podría ser:

```kotlin
data class UserResponse(
    val name: String,
    val phone: String?
)
```

El `?` expresa una decisión del dominio:

> El teléfono puede no existir.

No deberíamos poner `String?` automáticamente en todos lados.

Si un campo es obligatorio:

```kotlin
val email: String
```

Si realmente puede faltar:

```kotlin
val phone: String?
```

Esto hace que el modelo sea más expresivo.

---

# 8. `null` no es lo mismo que un valor por defecto

Es importante distinguir:

```kotlin
val description: String? = null
```

de:

```kotlin
val description: String = ""
```

El primero significa:

> La descripción puede no existir.

El segundo significa:

> La descripción siempre existe y su valor inicial es una cadena vacía.

No debemos agregar valores por defecto simplemente para evitar `null`.

La decisión depende del contrato de la API y de las reglas del negocio.

Debemos preguntarnos:

* ¿El campo es obligatorio?
* ¿Puede ser `null`?
* ¿Puede estar vacío?
* ¿Tiene un valor por defecto?
* ¿El valor por defecto tiene significado para el negocio?

---

# 9. Evitar `!!`

Kotlin permite:

```kotlin
val name: String? = getName()

println(name!!.length)
```

El operador `!!` significa:

> "Confío en que esto no es `null`."

Pero si efectivamente es `null`:

```text
NullPointerException
```

En backend conviene evitar `!!` siempre que sea posible.

Podemos utilizar:

```kotlin
name?.length
```

o:

```kotlin
name ?: defaultValue
```

o realizar una validación explícita:

```kotlin
requireNotNull(name)
```

---

# 10. `require`, `check` y `requireNotNull`

Kotlin proporciona funciones útiles para validar condiciones.

## `require`

Se utiliza principalmente para validar argumentos.

```kotlin
require(amount > BigDecimal.ZERO) {
    "Amount must be greater than zero"
}
```

Si la condición no se cumple:

```text
IllegalArgumentException
```

---

## `check`

Se utiliza para validar el estado de un objeto.

```kotlin
check(account.isActive) {
    "Account is inactive"
}
```

---

## `requireNotNull`

Permite exigir que un valor no sea `null`.

```kotlin
val user = requireNotNull(user) {
    "User is required"
}
```

Estas herramientas son útiles, aunque en nuestro backend también tendremos excepciones específicas para representar errores de negocio.

---

# 11. `enum class`

Los enums son útiles para representar un conjunto limitado de valores.

Por ejemplo:

```kotlin
enum class RechargeStatus {
    PENDING,
    COMPLETED,
    FAILED
}
```

Podemos utilizar:

```kotlin
val status = RechargeStatus.PENDING
```

Otro ejemplo:

```kotlin
enum class UserRole {
    USER,
    ADMIN
}
```

Esto es más seguro que:

```kotlin
val role = "ADMIN"
```

porque el compilador puede ayudarnos a detectar valores inválidos.

---

# 12. `sealed class`

Las `sealed class` permiten representar un conjunto cerrado de posibilidades.

Por ejemplo:

```kotlin
sealed class PaymentResult {

    data class Success(
        val transactionId: Long
    ) : PaymentResult()

    data class Failure(
        val reason: String
    ) : PaymentResult()
}
```

Podemos utilizar:

```kotlin
when (result) {

    is PaymentResult.Success ->
        println(result.transactionId)

    is PaymentResult.Failure ->
        println(result.reason)
}
```

El compilador conoce las posibilidades.

Las `sealed class` pueden ser útiles para representar resultados de operaciones.

No debemos utilizarlas para absolutamente todo.

---

# 13. Collections

Kotlin proporciona collections muy expresivas.

Por ejemplo:

```kotlin
val products = listOf(
    "Phone",
    "Tablet",
    "Laptop"
)
```

Podemos utilizar:

```kotlin
val names = products.map {
    it.uppercase()
}
```

También:

```kotlin
val expensiveProducts = products.filter {
    it.price > BigDecimal("100000")
}
```

En backend utilizaremos frecuentemente:

```text
List
Set
Map
```

Por ejemplo:

```kotlin
val products: List<Product>
```

o:

```kotlin
val productsById: Map<Long, Product>
```

---

# 14. `List` vs `MutableList`

Preferimos exponer colecciones inmutables cuando no necesitamos modificarlas.

Por ejemplo:

```kotlin
fun getProducts(): List<Product>
```

en lugar de:

```kotlin
fun getProducts(): MutableList<Product>
```

Esto reduce las posibilidades de modificar accidentalmente el estado.

`MutableList` no está prohibido.

La pregunta que debemos hacernos es:

> ¿Realmente necesito modificar esta colección?

---

# 15. Extension Functions

Las extension functions permiten agregar comportamiento a un tipo sin modificar su clase original.

Por ejemplo:

```kotlin
fun String.toSlug(): String =
    lowercase()
        .trim()
        .replace(" ", "-")
```

Entonces:

```kotlin
val slug = "Hello World".toSlug()
```

devuelve:

```text
hello-world
```

En backend pueden ser útiles para:

* conversiones;
* mappings;
* validaciones;
* transformaciones;
* utilidades específicas del dominio.

Por ejemplo:

```kotlin
fun User.toResponse(): UserResponse =
    UserResponse(
        id = id,
        name = name,
        email = email
    )
```

Esto puede hacer que el mapping sea muy legible.

---

# 16. Scope Functions

Kotlin tiene varias scope functions:

```text
let
run
with
apply
also
```

No necesitamos memorizarlas mecánicamente.

Lo importante es entender cuándo aportan claridad.

Por ejemplo:

```kotlin
val user = User().apply {
    name = "Juan"
    email = "juan@example.com"
}
```

`apply` es útil cuando queremos configurar un objeto.

Otro ejemplo:

```kotlin
user?.let {
    processUser(it)
}
```

`let` puede ser útil cuando queremos ejecutar algo solamente si existe un valor.

Pero existe una regla importante:

> **No utilizar scope functions solamente porque Kotlin las permite.**

Si:

```kotlin
user?.let {
    service.process(it)
}
```

es más difícil de leer que:

```kotlin
if (user != null) {
    service.process(user)
}
```

la segunda opción puede ser mejor.

La legibilidad es más importante que utilizar características avanzadas del lenguaje.

---

# 17. Excepciones

En backend vamos a trabajar frecuentemente con errores.

Por ejemplo:

```kotlin
throw IllegalArgumentException("Invalid amount")
```

Pero en aplicaciones reales probablemente tendremos excepciones específicas.

Por ejemplo:

```kotlin
class AccountNotFoundException(
    accountId: Long
) : RuntimeException(
    "Account $accountId not found"
)
```

O:

```kotlin
class InsufficientBalanceException :
    RuntimeException("Insufficient balance")
```

Más adelante Spring podrá interceptarlas y convertirlas en respuestas HTTP apropiadas.

Por ejemplo:

```text
AccountNotFoundException
        ↓
404 Not Found
```

o:

```text
InsufficientBalanceException
        ↓
409 Conflict
```

Esto lo veremos cuando estudiemos manejo global de excepciones.

---

# 18. Dinero: utilizar `BigDecimal`

Este concepto es especialmente importante para PayFlow.

No debemos representar dinero utilizando:

```kotlin
val amount: Double
```

Los tipos de punto flotante pueden producir errores de precisión.

Para dinero utilizaremos:

```kotlin
BigDecimal
```

Por ejemplo:

```kotlin
val amount = BigDecimal("1500.50")
```

Es preferible construir un `BigDecimal` desde un `String`:

```kotlin
BigDecimal("1500.50")
```

en lugar de partir de un `Double`.

Por ejemplo:

```kotlin
BigDecimal(1500.50)
```

puede introducir en el `BigDecimal` las imprecisiones propias de la representación del `Double`.

En nuestro proyecto:

```kotlin
data class CreateRechargeRequest(
    val amount: BigDecimal,
    val productId: Long
)
```

---

# 19. `suspend` y Coroutines

Si venís de Android, probablemente ya conozcas coroutines.

En backend Kotlin también podemos utilizarlas.

Una función suspendida:

```kotlin
suspend fun getUser(id: Long): User {
    // ...
}
```

puede suspenderse mientras espera una operación sin bloquear necesariamente el thread durante esa espera.

Esto es especialmente útil para operaciones de I/O:

```text
Database
HTTP
File System
External APIs
```

La idea fundamental es:

> `suspend` no significa automáticamente "crear un thread".

Una coroutine es un mecanismo para manejar operaciones concurrentes de forma eficiente, y una función `suspend` puede suspender su ejecución y reanudarla posteriormente.

---

# 20. ¿Coroutines o código bloqueante?

Spring Boot soporta diferentes modelos de programación.

Podemos trabajar con APIs tradicionales:

```kotlin
fun getUser(id: Long): User
```

o con APIs suspendidas:

```kotlin
suspend fun getUser(id: Long): User
```

No debemos elegir coroutines simplemente porque Kotlin las tenga.

La elección depende de:

* stack utilizado;
* driver de base de datos;
* librerías;
* modelo de concurrencia;
* necesidades del proyecto;
* complejidad;
* experiencia del equipo.

En nuestro curso veremos ambos enfoques antes de decidir qué utilizaremos en PayFlow.

---

# 21. Kotlin + Spring

Kotlin y Spring funcionan muy bien juntos, pero Spring nació dentro del ecosistema Java.

Spring utiliza mecanismos como:

* reflection;
* proxies;
* generación de clases;
* configuración automática.

Kotlin tiene algunas características que debemos tener en cuenta.

Por ejemplo:

> Las clases Kotlin son `final` por defecto.

Spring, en determinados casos, necesita poder crear proxies sobre determinadas clases.

Por eso el proyecto Kotlin + Spring suele utilizar el plugin:

```text
kotlin("plugin.spring")
```

Este plugin facilita la integración con Spring.

Cuando creemos nuestro proyecto con Spring Initializr veremos esta configuración.

No necesitamos memorizarla todavía.

---

# 22. Kotlin + JPA

Otra consideración importante aparece cuando utilizamos JPA/Hibernate.

Una entidad podría eventualmente verse conceptualmente así:

```kotlin
@Entity
class User(
    @Id
    @GeneratedValue
    val id: Long? = null,

    val name: String
)
```

JPA tiene determinadas expectativas respecto a las entidades:

* constructores;
* proxies;
* clases no finales;
* propiedades;
* generación de IDs.

Por eso Kotlin necesita ciertas configuraciones/plugins para trabajar cómodamente con JPA.

Esto será más importante cuando estudiemos:

```text
Spring Data JPA
+
Hibernate
+
PostgreSQL
```

Por ahora es suficiente entender que:

> **Kotlin + Spring no es simplemente Java escrito con otra sintaxis.**

Existen decisiones específicas del ecosistema Kotlin.

---

# 23. Kotlin idiomático vs Kotlin complicado

Una de las trampas al aprender Kotlin es intentar utilizar todas sus características.

Podemos escribir código extremadamente compacto:

```kotlin
fun process(user: User?) =
    user?.takeIf { it.active }
        ?.let(::processUser)
        ?: throw IllegalStateException()
```

Es válido.

Pero eso no significa que sea mejor.

A veces:

```kotlin
fun process(user: User?) {

    if (user == null) {
        throw IllegalArgumentException("User is required")
    }

    if (!user.active) {
        throw IllegalStateException("User is inactive")
    }

    processUser(user)
}
```

puede ser mucho más fácil de entender.

En un proyecto profesional:

> **Claridad > cantidad de features de Kotlin utilizadas.**

---

# 24. Aplicándolo a PayFlow

Nuestro código eventualmente tendrá objetos como:

```kotlin
data class CreateRechargeRequest(
    val amount: BigDecimal,
    val productId: Long
)
```

Estados:

```kotlin
enum class RechargeStatus {
    PENDING,
    COMPLETED,
    FAILED
}
```

Respuestas:

```kotlin
data class RechargeResponse(
    val id: Long,
    val amount: BigDecimal,
    val status: RechargeStatus
)
```

Errores:

```kotlin
class AccountNotFoundException(
    accountId: Long
) : RuntimeException(
    "Account $accountId not found"
)
```

Y posiblemente resultados:

```kotlin
sealed class RechargeResult {

    data class Success(
        val rechargeId: Long
    ) : RechargeResult()

    data class Failure(
        val reason: String
    ) : RechargeResult()
}
```

No vamos a utilizar todos estos conceptos desde el primer día.

Los iremos incorporando cuando exista una necesidad real.

---

# 🧪 Ejercicio

Vamos a trabajar con un pequeño modelo de PayFlow.

Tenemos:

```kotlin
data class CreatePaymentRequest(
    val amount: Double,
    val description: String?,
    val type: String
)
```

## Pregunta 1

¿Qué problemas encontrás en este DTO?

Pensá especialmente en:

* `Double`;
* `String?`;
* `String` para representar tipos de pago.

---

## Pregunta 2

¿Cómo lo rediseñarías utilizando:

* `BigDecimal`;
* `enum class`;
* tipos apropiados de Kotlin?

---

## Pregunta 3

Tenemos:

```kotlin
fun findUser(id: Long): User? {
    // ...
}
```

Y alguien escribe:

```kotlin
val user = findUser(10)!!
```

¿Por qué podría ser peligroso?

¿Cómo lo escribirías de una manera más segura?

---

## Pregunta 4

Tenemos:

```kotlin
enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED
}
```

¿Por qué podría ser mejor que:

```kotlin
val status: String
```

?

---

## Pregunta 5

¿Cuándo utilizarías `data class`?

Dame un ejemplo relacionado con nuestro backend.

---

## Ejercicio práctico

Diseñá un DTO para crear una **recarga de celular**.

Debe tener:

* monto;
* número de teléfono;
* operador;
* tipo de recarga.

El operador solamente puede ser:

```text
MOVISTAR
PERSONAL
CLARO
```

El tipo solamente puede ser:

```text
PREPAID
POSTPAID
```

Definí:

* `enum class Operator`;
* `enum class RechargeType`;
* `data class CreateRechargeRequest`.

---

# 🎤 Preguntas de entrevista

### Kotlin

> ¿Cuál es la diferencia entre `val` y `var`?

### Kotlin

> ¿Qué problema resuelve null safety?

### Kotlin

> ¿Qué diferencia existe entre `data class` y una clase normal?

### Backend

> ¿Por qué utilizarías `BigDecimal` para dinero?

### Kotlin

> ¿Qué es una extension function?

### Kotlin

> ¿Qué es una `sealed class` y cuándo podría ser útil?

### Coroutines

> ¿Qué significa que una función sea `suspend`?

### Spring + Kotlin

> ¿Por qué Kotlin necesita configuraciones/plugins específicos para trabajar con Spring y JPA?

### Arquitectura

> ¿Por qué no deberíamos utilizar nuestras entidades JPA directamente como DTOs de la API?

---

# 🧠 Lo que debo poder explicar sin mirar

Al terminar esta clase debería poder explicar:

* Por qué preferimos `val` cuando sea posible.
* Qué es una `data class`.
* Qué es un DTO.
* Qué diferencia existe entre DTO y Entity.
* Cómo funciona null safety.
* Por qué debemos evitar `!!`.
* Qué diferencia existe entre `null` y un valor por defecto.
* Cuándo utilizar `enum class`.
* Cuándo podría ser útil `sealed class`.
* Diferencias entre `List` y `MutableList`.
* Qué son las extension functions.
* Para qué sirven las principales scope functions.
* Cómo manejar excepciones.
* Por qué usamos `BigDecimal` para dinero.
* Qué significa `suspend`.
* Por qué `suspend` no significa automáticamente crear un thread.
* Qué relación existe entre Kotlin, Spring y JPA.
* Por qué no debemos escribir Kotlin excesivamente complejo.
* Por qué DTO y Entity deberían tener responsabilidades separadas.

---

# ☑️ Checklist

* [ ] Entiendo `val` vs `var`.
* [ ] Entiendo `data class`.
* [ ] Entiendo qué es un DTO.
* [ ] Entiendo DTO vs Entity.
* [ ] Entiendo null safety.
* [ ] Evito `!!` cuando no es necesario.
* [ ] Entiendo `require` y `check`.
* [ ] Entiendo `enum class`.
* [ ] Entiendo `sealed class`.
* [ ] Conozco `List`, `Set` y `Map`.
* [ ] Entiendo `List` vs `MutableList`.
* [ ] Entiendo extension functions.
* [ ] Entiendo las principales scope functions.
* [ ] Entiendo el manejo de excepciones.
* [ ] Sé por qué usamos `BigDecimal` para dinero.
* [ ] Entiendo qué significa `suspend`.
* [ ] Entiendo que coroutines no significa automáticamente crear threads.
* [ ] Conozco las particularidades de Kotlin + Spring.
* [ ] Conozco las particularidades de Kotlin + JPA.
* [ ] Completé el ejercicio.
* [ ] Respondí las preguntas de entrevista.

---

# 🏁 Estado de la clase

**🟢 COMPLETADA**

La clase se considera completada cuando:

1. Respondí el ejercicio.
2. Revisamos mis respuestas.
3. Aclaramos las dudas que aparecieron.
4. Incorporamos al documento los conceptos importantes que surgieron durante la práctica.

---

# 🚀 Próxima clase

## Clase 3 — Spring Framework: IoC, Dependency Injection y Beans

En la siguiente clase empezaremos con **Spring de verdad**.

Vamos a entender:

```text
Spring Framework
       │
       ├── IoC
       │
       ├── Dependency Injection
       │
       ├── Application Context
       │
       └── Beans
```

Y finalmente crearemos nuestro **primer proyecto Spring Boot con Kotlin**.
