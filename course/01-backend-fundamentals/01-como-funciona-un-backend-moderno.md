# Clase 1 — Cómo funciona un backend moderno

> **Fase:** 0 — Preparación
> **Estado:** 🟢 Completada
> **Proyecto:** PayFlow API

---

## 🎯 Objetivos

Al terminar esta clase deberías poder:

* Entender qué es un backend.
* Diferenciar cliente y servidor.
* Entender cómo se comunican mediante HTTP.
* Entender qué es una API.
* Entender qué es un endpoint.
* Conocer los principales métodos HTTP.
* Entender qué es una request y una response.
* Conocer los principales códigos de estado HTTP.
* Entender qué es JSON.
* Comprender el concepto de REST.
* Entender qué es la lógica de negocio.
* Comprender la separación Controller → Service → Repository.
* Entender qué papel cumple Spring Boot.
* Poder explicar el flujo completo de una petición HTTP.

---

# 1. ¿Qué es un backend?

Un **backend** es la parte de una aplicación que se ejecuta del lado del servidor.

Entre sus responsabilidades habituales están:

* procesar solicitudes;
* aplicar reglas de negocio;
* validar información;
* autenticarse y autorizar usuarios;
* acceder a bases de datos;
* comunicarse con otros servicios;
* procesar operaciones;
* devolver respuestas a los clientes.

Por ejemplo, imaginemos una aplicación de pagos.

El usuario quiere consultar su saldo:

```text
📱 Aplicación Android
        │
        │ "¿Cuál es mi saldo?"
        ▼
🌐 Backend
        │
        │ consulta
        ▼
🗄️ Base de datos
        │
        │ $150.000
        ▼
🌐 Backend
        │
        │ respuesta
        ▼
📱 Aplicación Android
```

El backend actúa como intermediario entre el cliente y los sistemas que contienen o procesan la información.

Una aplicación Android normalmente **no debería conectarse directamente a PostgreSQL**.

El backend permite controlar:

* quién puede acceder;
* qué operaciones puede realizar;
* qué datos puede consultar;
* qué reglas deben cumplirse;
* cómo se almacenan los datos.

---

# 2. Cliente vs servidor

En una arquitectura cliente-servidor tenemos dos actores principales.

## Cliente

El cliente es quien realiza una solicitud.

Puede ser:

* una aplicación Android;
* una aplicación iOS;
* una página web;
* otro backend;
* un sistema interno;
* un dispositivo IoT;
* una herramienta como Postman.

En nuestro caso podemos imaginar:

```text
Android App
```

como cliente.

## Servidor

El servidor recibe las solicitudes, las procesa y devuelve respuestas.

Nuestro servidor será construido utilizando:

```text
Kotlin
+
Spring Boot
```

Por lo tanto:

```text
Cliente
   │
   │ HTTP
   ▼
Servidor
   │
   │
   ├── lógica de negocio
   ├── seguridad
   ├── acceso a datos
   └── integraciones
```

---

# 3. ¿Cómo se comunican?

Una de las formas más comunes de comunicación entre cliente y servidor es **HTTP**.

HTTP significa:

**Hypertext Transfer Protocol**

En una API REST utilizaremos HTTP para enviar solicitudes y recibir respuestas.

Por ejemplo:

```http
GET /api/accounts/123
```

El cliente está solicitando información relacionada con la cuenta `123`.

El servidor podría responder:

```http
200 OK
```

con:

```json
{
  "id": 123,
  "balance": 150000.00,
  "currency": "ARS"
}
```

El protocolo HTTP define una estructura común para estas comunicaciones.

---

# 4. ¿Qué es una API?

API significa:

**Application Programming Interface**

En nuestro contexto, podemos pensar en una API como un **contrato que permite que diferentes sistemas se comuniquen con nuestro backend**.

Por ejemplo:

```http
GET /api/accounts/123
```

puede representar:

> "Quiero consultar la cuenta 123."

Otro endpoint:

```http
POST /api/accounts/123/recharges
```

puede representar:

> "Quiero realizar una nueva recarga sobre la cuenta 123."

Una API define aspectos como:

* qué operaciones existen;
* qué endpoints están disponibles;
* qué datos recibe cada operación;
* qué datos devuelve;
* qué errores pueden producirse;
* qué autenticación requiere;
* qué usuarios pueden utilizarla.

---

# 5. ¿Qué es un endpoint?

Un **endpoint** es un punto específico de acceso de una API.

Por ejemplo:

```http
GET /api/products
```

es un endpoint.

También:

```http
GET /api/products/123
```

es otro endpoint.

Podríamos tener:

```text
GET    /api/products
GET    /api/products/{id}
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

Cada uno representa una operación diferente.

Es importante distinguir:

```text
API
```

de:

```text
Endpoint
```

La API es el conjunto de contratos y operaciones que exponemos.

Un endpoint es un punto específico dentro de esa API.

---

# 6. HTTP Methods

Los métodos HTTP indican qué tipo de operación queremos realizar.

Los principales que utilizaremos son:

| Método   | Uso habitual                             |
| -------- | ---------------------------------------- |
| `GET`    | Obtener información                      |
| `POST`   | Crear un recurso o iniciar una operación |
| `PUT`    | Reemplazar o actualizar un recurso       |
| `PATCH`  | Actualizar parcialmente un recurso       |
| `DELETE` | Eliminar un recurso                      |

## GET

Por ejemplo:

```http
GET /api/products
```

Significa:

> Obtener productos.

## POST

```http
POST /api/products
```

Significa:

> Crear un nuevo producto.

También podemos utilizar POST para operaciones que generan una nueva acción o resultado:

```http
POST /api/accounts/123/recharges
```

Significa:

> Crear/ejecutar una nueva recarga.

Por eso no debemos pensar que:

```text
POST = modificar
```

Es más correcto pensar que POST se utiliza habitualmente para **crear un recurso o iniciar una operación que produce un nuevo resultado en el servidor**.

## PUT

```http
PUT /api/products/123
```

Puede utilizarse para actualizar/reemplazar el producto `123`.

## PATCH

```http
PATCH /api/products/123
```

Puede utilizarse para modificar parcialmente un producto.

## DELETE

```http
DELETE /api/products/123
```

Solicita eliminar el producto `123`.

---

# 7. Request

Una **request** es la petición que el cliente envía al servidor.

Conceptualmente podemos dividirla en:

```text
REQUEST
│
├── Method
├── URL
├── Headers
└── Body
```

Por ejemplo:

```http
POST /api/users
Content-Type: application/json
```

Body:

```json
{
  "name": "Juan",
  "email": "juan@example.com"
}
```

Tenemos:

### Method

```text
POST
```

### URL

```text
/api/users
```

### Header

```text
Content-Type: application/json
```

### Body

```json
{
  "name": "Juan",
  "email": "juan@example.com"
}
```

No todas las requests tienen body.

Por ejemplo, un `GET` normalmente puede no necesitarlo:

```http
GET /api/products/123
```

---

# 8. Response

Una **response** es la respuesta que el servidor devuelve al cliente.

Conceptualmente:

```text
RESPONSE
│
├── Status Code
├── Headers
└── Body
```

Por ejemplo:

```http
HTTP/1.1 201 Created
Content-Type: application/json
```

Body:

```json
{
  "id": 15,
  "name": "Super Recharge",
  "email": "juan@example.com"
}
```

El cliente recibe esa información y puede utilizarla para actualizar su interfaz o continuar con el flujo de la aplicación.

---

# 9. HTTP Status Codes

Los códigos de estado indican qué ocurrió al procesar una request.

Se agrupan principalmente en:

```text
1xx → Información
2xx → Éxito
3xx → Redirección
4xx → Error relacionado con la request
5xx → Error del servidor
```

Para nuestro curso, los más importantes serán:

## 2xx — Éxito

### 200 OK

La operación se realizó correctamente.

```text
GET /api/products
→ 200 OK
```

### 201 Created

Se creó un recurso.

```text
POST /api/products
→ 201 Created
```

### 204 No Content

La operación fue correcta pero no se devuelve contenido.

Por ejemplo:

```text
DELETE /api/products/123
→ 204 No Content
```

---

# 10. Errores 4xx

Estos códigos indican normalmente que existe un problema con la solicitud o con los permisos del cliente.

### 400 Bad Request

La request no es válida.

Por ejemplo:

```json
{
  "email": "esto-no-es-un-email"
}
```

### 401 Unauthorized

El cliente no está autenticado correctamente.

Por ejemplo:

```text
No JWT
```

o:

```text
JWT inválido
```

### 403 Forbidden

El cliente está autenticado pero **no tiene permiso** para realizar la operación.

Por ejemplo:

```text
Usuario autenticado
        ↓
Intenta acceder a endpoint de ADMIN
        ↓
403 Forbidden
```

Una distinción importante:

```text
401 ≠ 403
```

**401:** no estás autenticado.

**403:** estás autenticado, pero no tenés permiso.

### 404 Not Found

El recurso solicitado no existe.

```text
GET /api/products/999999
→ 404 Not Found
```

### 409 Conflict

Existe un conflicto con el estado actual del sistema.

Por ejemplo:

```text
Intentar registrar un email
que ya está registrado.
```

---

# 11. Errores 5xx

Indican problemas del lado del servidor.

### 500 Internal Server Error

Se produjo un error inesperado en el servidor.

### 503 Service Unavailable

El servicio no está disponible temporalmente.

Por ejemplo, podría existir un problema con una dependencia externa.

---

# 12. JSON

JSON significa:

**JavaScript Object Notation**

Es uno de los formatos más utilizados para intercambiar información entre clientes y APIs.

Por ejemplo:

```json
{
  "id": 10,
  "name": "Super Recharge",
  "price": 1500.00
}
```

En Kotlin podríamos representar esa información mediante:

```kotlin
data class Product(
    val id: Long,
    val name: String,
    val price: BigDecimal
)
```

Más adelante veremos cómo Spring convierte automáticamente entre objetos Kotlin y JSON.

Conceptualmente:

```text
JSON
 ↓
Objeto Kotlin
```

y:

```text
Objeto Kotlin
 ↓
JSON
```

Este proceso se conoce como **serialización y deserialización**.

---

# 13. ¿Qué es REST?

REST significa:

**Representational State Transfer**

REST no es una librería ni un framework.

Es un conjunto de principios para diseñar APIs utilizando recursos y operaciones HTTP.

Por ejemplo, podemos pensar en:

```text
/products
```

como un recurso.

Entonces:

```http
GET /products
```

obtiene productos.

```http
GET /products/10
```

obtiene el producto `10`.

```http
POST /products
```

crea un producto.

```http
PUT /products/10
```

actualiza/reemplaza el producto `10`.

```http
DELETE /products/10
```

elimina el producto `10`.

Una API REST bien diseñada busca tener una estructura consistente y predecible.

---

# 14. ¿Qué ocurre cuando hacemos una petición?

Supongamos que nuestra aplicación Android ejecuta:

```http
GET https://api.payflow.com/api/accounts/123
```

El flujo simplificado sería:

```text
📱 Android
     │
     │ HTTP Request
     ▼
🌐 Internet
     │
     ▼
🖥️ Servidor
     │
     ▼
🌱 Spring Boot
     │
     ▼
🎯 Controller
     │
     ▼
⚙️ Service
     │
     ▼
📦 Repository
     │
     ▼
🗄️ PostgreSQL
```

Después la información vuelve:

```text
🗄️ PostgreSQL
     │
     ▼
📦 Repository
     │
     ▼
⚙️ Service
     │
     ▼
🎯 Controller
     │
     ▼
🌱 Spring Boot
     │
     ▼
📱 Android
```

Este flujo representa una arquitectura simplificada. En una aplicación real existen más componentes, pero nos sirve como modelo mental inicial.

---

# 15. ¿Dónde entra Spring Boot?

Spring Boot será la tecnología principal que utilizaremos para construir nuestro backend.

Nos proporciona infraestructura para:

* levantar nuestra aplicación;
* recibir peticiones HTTP;
* crear y administrar objetos;
* Dependency Injection;
* configuración;
* crear APIs REST;
* acceder a bases de datos;
* seguridad;
* testing;
* observabilidad.

En lugar de construir manualmente toda la infraestructura de un servidor, Spring Boot nos proporciona un ecosistema preparado para aplicaciones empresariales.

Más adelante veremos que Spring Boot se apoya en Spring Framework.

Una de las ideas fundamentales de Spring es:

```text
Inversion of Control
        +
Dependency Injection
```

Estas ideas serán el foco de las próximas clases.

---

# 16. Business Logic

Una API no solamente recibe y devuelve datos.

El backend también contiene las **reglas de negocio**.

Por ejemplo:

> Un usuario no puede realizar una recarga si su cuenta está bloqueada.

Otra regla podría ser:

> Una recarga no puede superar el límite diario del usuario.

Otra:

> Un producto no puede venderse si está deshabilitado.

Estas reglas forman parte de la lógica de negocio.

Podemos representarlo así:

```text
Usuario solicita recarga
        │
        ▼
¿Cuenta activa?
        │
    ┌───┴───┐
   NO       SÍ
    │        │
  ERROR      ▼
        ¿Producto disponible?
              │
          ┌───┴───┐
         NO       SÍ
          │        │
        ERROR      ▼
              ¿Límite permitido?
                    │
                   ...
```

La lógica de negocio debería estar organizada de manera que pueda mantenerse, probarse y reutilizarse.

---

# 17. Arquitectura en capas

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

Cada capa tiene una responsabilidad.

---

## Controller

El Controller se ocupa principalmente de la comunicación HTTP.

Por ejemplo:

```text
Request
   ↓
Controller
   ↓
Response
```

Sus responsabilidades pueden incluir:

* recibir requests;
* interpretar parámetros;
* recibir DTOs;
* devolver responses;
* utilizar códigos HTTP apropiados.

No debería contener toda la lógica de negocio.

---

## Service

El Service contiene principalmente la lógica de negocio.

Por ejemplo:

```text
RechargeService
```

podría encargarse de:

* comprobar que la cuenta está activa;
* validar límites;
* comprobar disponibilidad;
* ejecutar la operación;
* coordinar diferentes repositorios;
* aplicar una transacción.

---

## Repository

El Repository se ocupa del acceso a los datos.

Conceptualmente:

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

---

# 18. ¿Por qué separar las capas?

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

### Importante

La separación en capas no significa que haya que crear clases innecesariamente.

La arquitectura debe **ayudar al proyecto**, no convertirse en burocracia.

---

# 19. PayFlow — nuestro proyecto

Durante este curso vamos a construir:

## PayFlow API

Una plataforma ficticia de pagos y recargas.

Inicialmente tendremos conceptos como:

```text
User
Product
Account
Transaction
```

Y progresivamente agregaremos:

```text
Authentication
Authorization
JWT
Roles
Permissions
Payments
Recharges
Audit
Reports
Testing
Docker
CI/CD
```

La arquitectura evolucionará durante el curso.

No construiremos todo de una vez.

Cada nueva clase agregará una pieza.

---

# 20. Arquitectura inicial de PayFlow

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

# 🧪 Ejercicio

Todavía no necesitamos escribir código.

Imaginá que un usuario desde Android quiere realizar:

```http
POST /api/accounts/123/recharges
```

con el siguiente body:

```json
{
  "amount": 5000,
  "productId": 25
}
```

Respondé las siguientes preguntas:

### 1.

¿Quién genera la request?

### 2.

¿Qué método HTTP estamos utilizando y por qué?

### 3.

¿Qué información viaja en el body?

### 4.

Una vez que Spring recibe la petición, ¿qué componente debería encargarse de recibirla?

### 5.

¿Dónde debería estar la regla de negocio que determina si la recarga puede realizarse?

### 6.

¿Qué componente debería encargarse de acceder a PostgreSQL?

---

# 🎤 Preguntas de entrevista

Intentá responder estas preguntas sin mirar el material.

## Nivel básico

### 1. ¿Qué es un backend?

### 2. ¿Qué es una API?

### 3. ¿Qué es un endpoint?

---

## Nivel intermedio

### 4. ¿Cuál es la diferencia entre GET y POST?

### 5. ¿Qué diferencia existe entre una request y una response?

### 6. ¿Cuál es la diferencia entre 401 y 403?

### 7. ¿Qué es REST?

---

## Nivel avanzado

### 8. ¿Por qué no deberíamos colocar toda la lógica de negocio dentro de un Controller?

### 9. ¿Qué responsabilidad debería tener un Service?

### 10. ¿Qué responsabilidad debería tener un Repository?

### 11. ¿Qué ventajas obtenemos al separar Controller, Service y Repository?

---

# 🧠 Lo que debo poder explicar sin mirar

Al terminar esta clase debería poder explicar con mis propias palabras:

* Qué es un backend.
* Qué diferencia existe entre cliente y servidor.
* Cómo se comunican mediante HTTP.
* Qué es una API.
* Qué es un endpoint.
* Qué diferencia existe entre GET, POST, PUT, PATCH y DELETE.
* Qué es una request.
* Qué es una response.
* Qué representa un código HTTP.
* Qué diferencia existe entre 401 y 403.
* Qué es JSON.
* Qué significa REST.
* Qué es Business Logic.
* Por qué utilizamos Controller, Service y Repository.
* Qué papel cumple Spring Boot.
* Cómo fluye una petición desde Android hasta PostgreSQL y de regreso.

---

# 📝 Resumen

El modelo mental principal de esta clase es:

```text
CLIENT
   │
   │ HTTP Request
   ▼
CONTROLLER
   │
   ▼
SERVICE
   │
   ▼
REPOSITORY
   │
   ▼
DATABASE
   │
   ▼
RESPONSE
```

Cada capa tiene una responsabilidad diferente:

```text
Controller  → HTTP
Service     → Business Logic
Repository  → Data Access
Database    → Persistence
```

Spring Boot nos proporcionará la infraestructura para construir este backend de manera organizada.

---

# ☑️ Checklist

* [x] Entiendo qué es un backend.
* [x] Entiendo cliente vs servidor.
* [x] Entiendo HTTP.
* [x] Entiendo Request y Response.
* [x] Entiendo JSON.
* [x] Entiendo REST.
* [x] Entiendo qué es un endpoint.
* [x] Conozco los principales HTTP Methods.
* [x] Conozco los principales Status Codes.
* [x] Entiendo la diferencia entre 401 y 403.
* [x] Entiendo qué es Business Logic.
* [x] Entiendo Controller / Service / Repository.
* [x] Entiendo dónde entra Spring Boot.
* [x] Puedo explicar el flujo completo de una petición.
* [x] Respondí el ejercicio.
* [x] Respondí las preguntas de entrevista.

---

# 🏁 Estado de la clase

**🟢 COMPLETADA**

### Próxima clase

**Clase 2 — Kotlin para Backend**

Vamos a revisar las características de Kotlin que son especialmente importantes para trabajar con Spring Boot, incluyendo `data class`, null safety, `BigDecimal`, `sealed class`, excepciones, colecciones, coroutines y particularidades de Kotlin con JPA.
