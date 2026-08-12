# Clase 7 — Configuration y Profiles

> **Fase:** 1 — Fundamentos de Spring
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

# Objetivos

Al terminar esta clase deberías poder:

* Configurar una aplicación Spring Boot con `application.properties` o `application.yml`.
* Leer propiedades con `@Value`.
* Agrupar propiedades con `@ConfigurationProperties`.
* Entender qué son los Spring Profiles.
* Activar perfiles para distintos entornos.
* Separar configuración por ambiente: `dev`, `test`, `prod`.
* Comprender cuándo usar variables de entorno.

---

# 1. ¿Por qué necesitamos configuración?

Una misma aplicación debe comportarse de forma distinta según el entorno.

Por ejemplo:

```text
Desarrollo (dev)
   └── Base de datos local

Testing (test)
   └── Base de datos en memoria

Producción (prod)
   └── Base de datos real
```

También necesitamos valores que cambian sin modificar código:

```text
puerto del servidor
URL de servicios externos
credenciales
tiempos de expiración
límites de paginación
```

Spring Boot nos permite externalizar toda esta configuración.

---

# 2. `application.properties`

El archivo más común para configurar Spring Boot es:

```text
src/main/resources/application.properties
```

Ejemplo:

```properties
server.port=8080
spring.application.name=payflow-api
payflow.api.version=v1
```

Cada línea representa una propiedad con un valor.

Este archivo se carga automáticamente cuando arranca la aplicación.

---

# 3. `application.yml`

También podemos usar formato YAML:

```text
src/main/resources/application.yml
```

Ejemplo:

```yaml
server:
  port: 8080

spring:
  application:
    name: payflow-api

payflow:
  api:
    version: v1
```

YAML es más legible cuando hay muchas propiedades anidadas.

Ambos formatos son válidos. En este curso usaremos `application.yml` por claridad.

---

# 4. Leer propiedades con `@Value`

Podemos inyectar una propiedad directamente en una clase:

```kotlin
@Service
class PayflowService(
    @Value("\${payflow.api.version}") private val apiVersion: String
)
```

Spring busca la propiedad `payflow.api.version` y le asigna el valor.

Ejemplo de uso:

```kotlin
@Service
class PayflowService(
    @Value("\${payflow.api.version}") private val apiVersion: String
) {

    fun getVersion(): String {
        return apiVersion
    }
}
```

`@Value` es útil para propiedades simples.

---

# 5. Agrupar propiedades con `@ConfigurationProperties`

Cuando tenemos varias propiedades relacionadas, es mejor agruparlas.

Supongamos:

```yaml
payflow:
  pagination:
    default-page-size: 20
    max-page-size: 100
```

Podemos crear una clase de configuración:

```kotlin
package com.payflow.api.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "payflow.pagination")
data class PaginationProperties(
    val defaultPageSize: Int,
    val maxPageSize: Int
)
```

Y habilitarla con `@EnableConfigurationProperties`:

```kotlin
package com.payflow.api.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(PaginationProperties::class)
class PropertiesConfig
```

Ahora podemos inyectar `PaginationProperties` donde la necesitemos:

```kotlin
@Service
class RechargeService(
    private val paginationProperties: PaginationProperties
)
```

Esto es más limpio que tener muchos `@Value` sueltos.

---

# 6. ¿Qué es un Profile?

Un **Profile** en Spring permite cargar configuración o Beans específicos según el entorno.

Por ejemplo:

```text
dev    → desarrollo local
test   → tests automatizados
prod   → producción
```

Podemos crear archivos como:

```text
application-dev.yml
application-test.yml
application-prod.yml
```

Cada archivo se carga solo cuando el profile correspondiente está activo.

---

# 7. Activar un Profile

Hay varias formas de activar un profile.

## Desde `application.yml`

```yaml
spring:
  profiles:
    active: dev
```

## Desde variables de entorno

```bash
SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
```

## Desde línea de comandos

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

---

# 8. Ejemplo con perfiles

`application-dev.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/payflow_dev
    username: postgres
    password: postgres
```

`application-prod.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USER}
    password: ${DATABASE_PASSWORD}
```

En producción no queremos escribir credenciales en el código.

Usamos variables de entorno como `${DATABASE_URL}`.

---

# 9. Beans condicionales por Profile

Podemos definir Beans que solo existan en determinado entorno.

```kotlin
package com.payflow.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class NotificationConfig {

    @Bean
    @Profile("dev")
    fun consoleNotificationSender(): NotificationSender {
        return ConsoleNotificationSender()
    }

    @Bean
    @Profile("prod")
    fun emailNotificationSender(): NotificationSender {
        return EmailNotificationSender()
    }
}
```

En `dev` usaremos un emisor de notificaciones que imprime en consola.

En `prod` usaremos el emisor real.

Esto es muy útil para tests y desarrollo local.

---

# 10. Variables de entorno

En producción, la configuración sensible no debe estar en el repositorio.

Spring permite leer variables de entorno fácilmente:

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
```

Si la variable de entorno existe, Spring la utiliza.

También podemos proveer un valor por defecto:

```yaml
server:
  port: ${PORT:8080}
```

Si `PORT` no está definida, se usará `8080`.

---

# 11. Buenas prácticas

* Separar configuración por ambiente usando profiles.
* No commitear credenciales ni secrets.
* Usar `@ConfigurationProperties` para propiedades relacionadas.
* Usar `@Value` solo para valores simples y puntuales.
* Proveer valores por defecto razonables para desarrollo local.
* Documentar las variables de entorno requeridas en `README.md` o `docs/`.

---

# Ejercicios

Resolvé estos ejercicios en tu cuaderno.

## Ejercicio 1

¿Cuál es la diferencia entre `application.properties` y `application.yml`?

## Ejercicio 2

Tenemos:

```yaml
payflow:
  recharge:
    min-amount: 100
    max-amount: 50000
```

Creá una clase `RechargeProperties` usando `@ConfigurationProperties` para agrupar estos valores.

## Ejercicio 3

¿Qué es un Spring Profile y para qué se utiliza?

## Ejercicio 4

¿Cómo activarías el profile `dev` al ejecutar la aplicación?

## Ejercicio 5

¿Por qué en producción usamos variables de entorno en lugar de escribir valores directamente en `application-prod.yml`?

## Ejercicio 6

Diseñá una configuración donde en `dev` se use un `FakePaymentGateway` y en `prod` se use un `RealPaymentGateway`.

---

# Preguntas de entrevista

1. ¿Cómo configurás una aplicación Spring Boot?
2. ¿Qué diferencia hay entre `@Value` y `@ConfigurationProperties`?
3. ¿Qué son los Spring Profiles?
4. ¿Cómo activás un profile en Spring Boot?
5. ¿Por qué es importante no hardcodear credenciales en `application.yml`?
6. ¿Cómo leés una variable de entorno en Spring Boot?

---

# Resumen

```text
Configuración
   │
   ├── application.yml
   ├── @Value
   └── @ConfigurationProperties

Profiles
   │
   ├── dev / test / prod
   ├── application-{profile}.yml
   └── @Profile

Variables de entorno
   │
   └── ${VARIABLE:default}
```

---

# Checklist

* [ ] Entiendo para qué sirve la configuración externa.
* [ ] Sé usar `application.yml`.
* [ ] Sé leer propiedades con `@Value`.
* [ ] Sé agrupar propiedades con `@ConfigurationProperties`.
* [ ] Entiendo qué es un Spring Profile.
* [ ] Sé activar un profile.
* [ ] Sé crear archivos `application-{profile}.yml`.
* [ ] Entiendo por qué usar variables de entorno.
* [ ] Completé los ejercicios.
* [ ] Revisé las preguntas de entrevista.

---

# Estado de la clase

**🟢 COMPLETADA**

---

# Próxima clase

## Clase 8 — Arquitectura de una aplicación Spring

Vamos a cerrar la fase 1 viendo cómo se organiza una aplicación Spring en capas y cómo se conectan Controller, Service y Repository.
