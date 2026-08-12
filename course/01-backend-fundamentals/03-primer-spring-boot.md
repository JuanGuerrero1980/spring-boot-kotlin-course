# Clase 3 — Primer proyecto Spring Boot con Kotlin

> **Fase:** 0 — Preparación
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Crear un proyecto Spring Boot con Kotlin.
* Entender Spring Initializr.
* Entender la estructura básica de un proyecto Gradle.
* Entender `build.gradle.kts`.
* Entender `settings.gradle.kts`.
* Entender `src/main` y `src/test`.
* Entender `@SpringBootApplication`.
* Ejecutar una aplicación Spring Boot.
* Entender qué ocurre durante el startup.
* Crear nuestro primer `@RestController`.
* Crear un endpoint `GET`.
* Probar una request HTTP.
* Entender la relación entre HTTP y Spring MVC.
* Diferenciar Spring Framework de Spring Boot.

---

# 1. Nuestro proyecto

Durante todo el curso vamos a construir un proyecto llamado:

```text
PayFlow API
```

La idea es que no sea simplemente un proyecto para aprender annotations.

Vamos a construir progresivamente un backend que pueda representar un sistema real.

Conceptualmente:

```text
                    PayFlow API
                         │
          ┌──────────────┼──────────────┐
          │              │              │
       Users          Payments       Recharges
          │              │              │
          └──────────────┼──────────────┘
                         │
                     PostgreSQL
```

Más adelante agregaremos:

* autenticación;
* autorización;
* usuarios;
* pagos;
* recargas;
* validaciones;
* persistencia;
* PostgreSQL;
* manejo de errores;
* tests;
* Docker;
* documentación;
* seguridad.

Pero vamos paso a paso.

---

# 2. ¿Qué es Spring Boot?

Antes hablamos de Spring Framework.

Ahora aparece:

```text
Spring Boot
```

Spring Boot se construye sobre Spring Framework y simplifica la creación y configuración de aplicaciones Spring.

Podemos pensar:

```text
Spring Framework
       │
       └── Spring Boot
              │
              ├── auto-configuración
              ├── starters
              ├── servidor embebido
              └── convenciones
```

Spring Framework proporciona gran parte de la infraestructura.

Spring Boot facilita utilizar esa infraestructura para crear aplicaciones reales rápidamente.

---

# 3. Spring Framework vs Spring Boot

Es importante no confundir ambos conceptos.

## Spring Framework

Proporciona conceptos e infraestructura como:

* IoC;
* Dependency Injection;
* Beans;
* ApplicationContext;
* Spring MVC;
* Spring Data;
* Spring Security.

## Spring Boot

Se construye sobre Spring Framework y simplifica la creación y configuración de aplicaciones.

Entre otras cosas proporciona:

* auto-configuración;
* starters;
* servidor embebido;
* convenciones;
* configuración simplificada.

Por ejemplo, podemos crear una aplicación web y ejecutarla directamente:

```bash
./gradlew bootRun
```

sin tener que configurar manualmente un servidor externo.

> **Importante:** Spring Boot no es simplemente un plugin de Spring Framework. Es un proyecto construido sobre Spring que simplifica su utilización.

---

# 4. Spring Initializr

Para crear nuestro proyecto utilizaremos:

**Spring Initializr**

https://start.spring.io/

Es una herramienta que genera la estructura inicial de un proyecto Spring Boot.

Podemos elegir:

```text
Project
Language
Spring Boot
Group
Artifact
Packaging
Java
Dependencies
```

---

# 5. Configuración de PayFlow

Para nuestro proyecto utilizaremos:

```text
Project: Gradle - Kotlin
Language: Kotlin
Packaging: Jar
Java: 21
```

Group:

```text
com.payflow
```

Artifact:

```text
payflow-api
```

Name:

```text
payflow-api
```

Description:

```text
Backend API for PayFlow
```

Package name:

```text
com.payflow.api
```

Como dependencia inicial:

```text
Spring Web
```

No vamos a agregar todas las dependencias desde el principio.

La idea es incorporar cada tecnología cuando realmente entendamos para qué sirve.

---

# 6. Estructura inicial

Una vez generado el proyecto tendremos aproximadamente:

```text
payflow-api/
│
├── gradle/
│
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   └── resources/
│   │
│   └── test/
│       └── kotlin/
│
├── .gitignore
├── build.gradle.kts
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── ...
```

No hace falta memorizar todo todavía.

Vamos a entender cada parte.

---

# 7. `src/main`

Dentro de:

```text
src/main
```

tenemos el código principal de nuestra aplicación.

Conceptualmente:

```text
src/main
   │
   ├── kotlin
   │
   └── resources
```

---

# 8. `src/main/kotlin`

Acá estará nuestro código Kotlin.

Por ejemplo:

```text
src/main/kotlin/com/payflow/api/
```

y dentro tendremos:

```text
PayflowApplication.kt
```

Más adelante tendremos:

```text
controller/
service/
repository/
domain/
dto/
config/
exception/
```

Pero todavía no vamos a crear todas esas carpetas.

---

# 9. `src/main/resources`

Acá colocaremos recursos de la aplicación.

Por ejemplo:

```text
application.properties
```

o:

```text
application.yml
```

Más adelante configuraremos:

* puerto;
* base de datos;
* logs;
* Spring;
* variables de entorno;
* etc.

---

# 10. `src/test`

Esta carpeta contiene nuestros tests.

```text
src/test/kotlin
```

La estructura separa:

```text
src/main
```

de:

```text
src/test
```

Nuestros tests forman parte del proyecto, pero no son código de producción.

Más adelante veremos:

```text
Unit Tests
Integration Tests
Spring Boot Tests
```

---

# 11. `build.gradle.kts`

Este archivo es fundamental:

```text
build.gradle.kts
```

Le indica a Gradle cómo construir nuestro proyecto.

Entre otras cosas define:

* plugins;
* dependencias;
* configuración;
* tareas;
* versiones.

Podemos encontrar algo conceptualmente similar a:

```kotlin
plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
}
```

Y:

```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}
```

No necesitamos memorizar la sintaxis todavía.

Lo importante es entender:

> Gradle utiliza este archivo para saber cómo construir nuestra aplicación.

---

# 12. ¿Qué es Gradle?

Gradle es una herramienta de build.

Nos permite:

* compilar;
* ejecutar tests;
* administrar dependencias;
* empaquetar la aplicación;
* ejecutar tareas.

Por ejemplo:

```bash
./gradlew build
```

construye el proyecto.

```bash
./gradlew test
```

ejecuta los tests.

```bash
./gradlew bootRun
```

ejecuta nuestra aplicación Spring Boot.

---

# 13. Gradle Wrapper

El proyecto incluye:

```text
gradlew
```

y:

```text
gradlew.bat
```

Esto se conoce como:

> **Gradle Wrapper**

El Wrapper permite que el proyecto utilice una versión específica de Gradle sin depender de una instalación global determinada.

Por eso normalmente utilizaremos:

```bash
./gradlew
```

en lugar de:

```bash
gradle
```

---

# 14. `settings.gradle.kts`

Otro archivo importante:

```text
settings.gradle.kts
```

Se utiliza para configurar el proyecto Gradle y definir su nombre.

Por ejemplo:

```kotlin
rootProject.name = "payflow-api"
```

En proyectos grandes también puede utilizarse para definir módulos.

Por ahora no necesitamos modificarlo.

---

# 15. Nuestra Application

Spring Initializr generará algo parecido a:

```kotlin
package com.payflow.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PayflowApplication

fun main(args: Array<String>) {
    runApplication<PayflowApplication>(*args)
}
```

Este archivo es extremadamente importante.

---

# 16. `fun main`

Como cualquier aplicación Kotlin/JVM tenemos:

```kotlin
fun main(args: Array<String>) {
    // ...
}
```

Es el punto de entrada de nuestra aplicación.

Cuando ejecutamos:

```bash
./gradlew bootRun
```

la aplicación termina llegando a ese punto de entrada.

---

# 17. `runApplication`

Tenemos:

```kotlin
runApplication<PayflowApplication>(*args)
```

Esto inicia nuestra aplicación Spring Boot.

Simplificando:

```text
main()
  │
  ▼
runApplication()
  │
  ▼
Spring Boot inicia
  │
  ▼
ApplicationContext
  │
  ▼
Beans
  │
  ▼
Servidor web
  │
  ▼
Aplicación lista
```

---

# 18. `@SpringBootApplication`

Tenemos:

```kotlin
@SpringBootApplication
class PayflowApplication
```

Es importante distinguir:

```text
PayflowApplication
```

es la clase.

Mientras que:

```text
@SpringBootApplication
```

es una annotation aplicada a esa clase.

Esta annotation indica que esa clase es la configuración principal de nuestra aplicación Spring Boot.

Conceptualmente agrupa varias funcionalidades:

```text
@SpringBootApplication
        │
        ├── @SpringBootConfiguration
        ├── @EnableAutoConfiguration
        └── @ComponentScan
```

No necesitamos memorizar todavía todos los detalles internos.

Pero sí debemos entender el concepto.

---

# 19. `@ComponentScan`

Spring necesita encontrar componentes como:

```text
@Component
@Service
@Repository
@Controller
```

`@ComponentScan` participa en ese proceso.

Por defecto, Spring Boot comienza a buscar componentes desde el package donde está nuestra clase principal.

Por eso tendremos:

```text
com.payflow.api
```

como package raíz.

Por ejemplo:

```text
com.payflow.api
       │
       ├── PayflowApplication
       │
       ├── controller
       │
       ├── service
       │
       └── repository
```

Spring podrá encontrar esos componentes.

---

# 20. Ejecutar la aplicación

Desde la raíz del proyecto:

```bash
./gradlew bootRun
```

En Windows:

```text
gradlew.bat bootRun
```

Si todo está correcto veremos los logs de Spring Boot.

También veremos información relacionada con el servidor web y el puerto.

Por defecto:

```text
8080
```

---

# 21. Nuestro primer servidor

Cuando Spring Boot inicia nuestra aplicación web:

```text
PayFlow API
     │
     ▼
Embedded Web Server
     │
     ▼
Port 8080
```

Por eso podemos acceder a:

```text
http://localhost:8080
```

`localhost` significa nuestra propia máquina.

---

# 22. Crear nuestro primer Controller

Creamos:

```text
controller/
```

Dentro:

```text
PayflowController.kt
```

Código:

```kotlin
package com.payflow.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PayflowController {

    @GetMapping("/api/hello")
    fun hello(): String {
        return "Hello from PayFlow API"
    }
}
```

---

# 23. ¿Qué hace `@RestController`?

Tenemos:

```kotlin
@RestController
class PayflowController
```

Le estamos indicando a Spring:

> Esta clase manejará requests HTTP y sus métodos producirán respuestas para una API REST.

Spring detectará el Controller y lo registrará como Bean.

---

# 24. ¿Qué hace `@GetMapping`?

Tenemos:

```kotlin
@GetMapping("/api/hello")
```

Esto significa:

> Cuando llegue una request HTTP `GET` a `/api/hello`, ejecutá este método.

Entonces:

```text
GET /api/hello
       │
       ▼
PayflowController
       │
       ▼
hello()
```

---

# 25. Probar nuestro endpoint

Con la aplicación ejecutándose:

```bash
./gradlew bootRun
```

abrimos:

```text
http://localhost:8080/api/hello
```

Deberíamos obtener:

```text
Hello from PayFlow API
```

Acabamos de crear nuestra primera API REST con Kotlin + Spring Boot.

---

# 26. ¿Qué ocurrió internamente?

Cuando hacemos:

```text
GET /api/hello
```

el flujo simplificado es:

```text
Browser / Postman / curl
          │
          │ HTTP GET
          ▼
     Port 8080
          │
          ▼
    Spring Web
          │
          ▼
PayflowController
          │
          ▼
       hello()
          │
          ▼
"Hello from PayFlow API"
```

Este concepto conecta directamente con la Clase 1.

---

# 27. Relación con HTTP

Nuestra request:

```http
GET /api/hello
```

contiene principalmente:

```text
Method:
GET

Path:
/api/hello
```

Para este ejemplo no necesitamos Body.

Spring recibe esa request y encuentra el método:

```kotlin
@GetMapping("/api/hello")
```

---

# 28. ¿Por qué `GET`?

Estamos realizando una operación de lectura.

No estamos:

* creando un recurso;
* modificando un recurso;
* eliminando un recurso.

Por eso utilizamos:

```text
GET
```

Más adelante utilizaremos:

```text
GET
POST
PUT
PATCH
DELETE
```

dependiendo de la operación.

---

# 29. `curl`

También podemos probar nuestra API desde la terminal:

```bash
curl http://localhost:8080/api/hello
```

Respuesta:

```text
Hello from PayFlow API
```

Esto es importante porque no necesitamos un navegador para consumir una API.

---

# 30. Devolviendo JSON

Podemos modificar el Controller:

```kotlin
@RestController
class PayflowController {

    @GetMapping("/api/hello")
    fun hello(): Map<String, String> {
        return mapOf(
            "message" to "Hello from PayFlow API"
        )
    }
}
```

La respuesta será conceptualmente:

```json
{
  "message": "Hello from PayFlow API"
}
```

Spring utiliza Jackson para convertir objetos Kotlin/Java a JSON.

Más adelante utilizaremos `data class` para representar nuestros DTOs.

---

# 31. Primer DTO

En lugar de utilizar un `Map`, podemos crear una `data class`.

Creamos:

```text
dto/
```

y:

```text
HelloResponse.kt
```

```kotlin
package com.payflow.api.dto

data class HelloResponse(
    val message: String
)
```

Y nuestro Controller:

```kotlin
package com.payflow.api.controller

import com.payflow.api.dto.HelloResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PayflowController {

    @GetMapping("/api/hello")
    fun hello(): HelloResponse {
        return HelloResponse(
            message = "Hello from PayFlow API"
        )
    }
}
```

Esto conecta nuevamente con lo aprendido en la Clase 2 sobre `data class`.

---

# 32. Endpoint de Status

Vamos a crear nuestro segundo endpoint:

```text
GET /api/status
```

Podemos crear:

```kotlin
package com.payflow.api.dto

data class StatusResponse(
    val status: String,
    val application: String
)
```

Y en el Controller:

```kotlin
@GetMapping("/api/status")
fun status(): StatusResponse {
    return StatusResponse(
        status = "UP",
        application = "PayFlow API"
    )
}
```

La respuesta será:

```json
{
  "status": "UP",
  "application": "PayFlow API"
}
```

---

# 33. ¿Estamos haciendo Clean Architecture?

Todavía no.

Nuestra estructura actual podría ser:

```text
com.payflow.api
│
├── PayflowApplication.kt
│
├── controller
│   └── PayflowController.kt
│
└── dto
    ├── HelloResponse.kt
    └── StatusResponse.kt
```

No necesitamos crear 25 paquetes desde el primer día.

La arquitectura aparecerá progresivamente a medida que el proyecto necesite nuevas responsabilidades.

---

# 34. Primer principio importante

No queremos hacer esto:

```kotlin
@RestController
class RechargeController {

    @PostMapping("/recharges")
    fun recharge() {

        // validar usuario

        // validar saldo

        // consultar operador

        // guardar en DB

        // enviar notificación
    }
}
```

Eso fue precisamente uno de los problemas que identificamos en la Clase 1.

El Controller debe encargarse principalmente de:

```text
HTTP
 ↓
recibir request
 ↓
delegar
 ↓
devolver response
```

La lógica de negocio estará en:

```text
Service
```

Y el acceso a datos:

```text
Repository
```

Lo veremos progresivamente.

---

# 35. Estructura que construiremos

Finalmente queremos llegar aproximadamente a:

```text
com.payflow.api
│
├── PayflowApplication.kt
│
├── controller
│
├── service
│
├── repository
│
├── dto
│
├── domain
│
├── config
│
└── exception
```

Pero no vamos a crear todo ahora.

Cada paquete aparecerá cuando tenga sentido.

---

# 36. ¿Qué pasa cuando arrancamos Spring?

Podemos visualizarlo así:

```text
./gradlew bootRun
        │
        ▼
      main()
        │
        ▼
runApplication()
        │
        ▼
Spring Boot
        │
        ▼
ApplicationContext
        │
        ├── descubre Beans
        │
        ├── configura dependencias
        │
        ├── inicia Web Server
        │
        └── aplicación lista
```

Entonces:

```text
localhost:8080
```

queda escuchando requests HTTP.

---

# Ejercicios

Resolvé estos ejercicios en tu cuaderno o en un archivo de práctica. Las respuestas no están incluidas; las revisaremos en la próxima clase.

## Ejercicio 1

Explicá con tus palabras:

> ¿Qué diferencia existe entre Spring Framework y Spring Boot?

## Ejercicio 2

Tenemos:

```kotlin
@SpringBootApplication
class PayflowApplication
```

¿Qué función cumple `@SpringBootApplication`?

## Ejercicio 3

Describí el recorrido completo de una request:

```http
GET /api/users
```

desde el cliente hasta que Spring ejecuta el método del Controller.

## Ejercicio 4

¿Por qué este endpoint utiliza `GET`?

```kotlin
@GetMapping("/users")
fun users()
```

¿Qué método HTTP utilizarías para crear un nuevo usuario?

## Ejercicio 5

¿Qué problema tendría este Controller?

```kotlin
@RestController
class RechargeController {

    @PostMapping("/recharges")
    fun recharge(): String {

        // validar usuario
        // validar saldo
        // consultar operador
        // guardar en DB
        // enviar notificación

        return "ok"
    }
}
```

## Ejercicio 6

Crear un endpoint:

```text
GET /api/status
```

que devuelva:

```json
{
  "status": "UP",
  "application": "PayFlow API"
}
```

utilizando una `data class`.

---

# Preguntas de entrevista

Respondé estas preguntas sin mirar el material.

1. ¿Qué es Spring Boot?
2. ¿Qué hace `@SpringBootApplication`?
3. ¿Qué sucede cuando ejecutás una aplicación Spring Boot?
4. ¿Qué es Spring Initializr?
5. ¿Qué función cumple Gradle?
6. ¿Qué diferencia hay entre `src/main` y `src/test`?
7. ¿Qué hace `@RestController`?
8. ¿Qué hace `@GetMapping`?
9. ¿Qué sucede cuando llega un `GET /api/users`?

---

# Resumen

Los conceptos principales de esta clase pueden resumirse así:

```text
Spring Framework
       │
       ▼
Spring Boot
       │
       ▼
Application
       │
       ▼
ApplicationContext
       │
       ▼
Beans
       │
       ▼
Web Server
       │
       ▼
HTTP Request
       │
       ▼
Controller
       │
       ▼
Service
       │
       ▼
Repository
       │
       ▼
Database
```

Y respecto a nuestro proyecto:

```text
PayFlow API
│
├── Kotlin
├── Spring Boot
├── Gradle
├── Spring Web
│
└── API REST
```

---

# Checklist

* [ ] Creé el proyecto PayFlow API.
* [ ] Utilicé Kotlin.
* [ ] Utilicé Gradle Kotlin DSL.
* [ ] Configuré Java.
* [ ] Agregué Spring Web.
* [ ] Entiendo `build.gradle.kts`.
* [ ] Entiendo `settings.gradle.kts`.
* [ ] Entiendo `src/main`.
* [ ] Entiendo `src/test`.
* [ ] Entiendo `@SpringBootApplication`.
* [ ] Entiendo `runApplication`.
* [ ] Pude ejecutar `./gradlew bootRun`.
* [ ] La aplicación inicia correctamente.
* [ ] Creé `PayflowController`.
* [ ] Creé `GET /api/hello`.
* [ ] Probé el endpoint.
* [ ] Creé una `data class` para una respuesta.
* [ ] Creé `GET /api/status`.
* [ ] Entiendo el recorrido de una request.
* [ ] Completé los ejercicios.
* [ ] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

Conceptos dominados:

```text
✓ Spring Framework
✓ Spring Boot
✓ Spring Initializr
✓ Gradle
✓ Spring Application
✓ @SpringBootApplication
✓ ApplicationContext
✓ Embedded Server
✓ @RestController
✓ @GetMapping
✓ HTTP GET
✓ DTO
✓ JSON
✓ Request → Controller → Response
✓ Separación de responsabilidades
```

---

# Próxima clase

## Clase 4 — IoC: Inversion of Control

Vamos a entender uno de los conceptos centrales de Spring:

```text
Inversion of Control
```

Y por qué Spring se encarga de crear y administrar objetos de nuestra aplicación.
