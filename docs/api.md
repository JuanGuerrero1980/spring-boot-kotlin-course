# API

Documento vivo de endpoints y contratos de la API.

## Validaciones

Los request bodies de `POST`, `PUT` y `PATCH` sobre `/api/recharges` se validan con Bean Validation.

Reglas generales:

* `amount` debe ser mayor que cero (`@Positive`).
* `phoneNumber` no puede estar vacío (`@NotBlank`).
* `operator` y `type` no pueden ser nulos (`@NotNull`).
* `status` no puede ser nulo (`@NotNull`).

Si alguna validación falla, la API responde `400 Bad Request` con detalles del error.

## Endpoints actuales

### Health check

```http
GET /api/hello
```

Respuesta:

```json
{
  "message": "Hello from PayFlow API"
}
```

### Status

```http
GET /api/status
```

Respuesta:

```json
{
  "status": "UP",
  "application": "PayFlow API"
}
```

### Headers de prueba

```http
GET /api/headers
X-Request-Id: abc-123
```

Respuesta `200 OK`:

```json
{
  "method": "GET",
  "path": "/api/headers",
  "contentType": null,
  "receivedRequestId": "abc-123"
}
```

Además devuelve el header `X-Response-Id` con el valor recibido o un identificador generado.

### Crear recarga

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

Respuesta `201 Created`:

```json
{
  "id": 1,
  "amount": 1000,
  "phoneNumber": "3531234567",
  "operator": "PERSONAL",
  "type": "PREPAID",
  "status": "PENDING"
}
```

### Listar recargas

```http
GET /api/recharges
```

Respuesta `200 OK`:

```json
[
  {
    "id": 1,
    "amount": 1000,
    "phoneNumber": "3531234567",
    "operator": "PERSONAL",
    "type": "PREPAID",
    "status": "PENDING"
  }
]
```

### Obtener recarga por ID

```http
GET /api/recharges/{id}
```

Respuesta `200 OK` si existe:

```json
{
  "id": 1,
  "amount": 1000,
  "phoneNumber": "3531234567",
  "operator": "PERSONAL",
  "type": "PREPAID",
  "status": "PENDING"
}
```

Respuesta `404 Not Found` si no existe.

### Modificar estado de una recarga

```http
PATCH /api/recharges/{id}
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

Respuesta `200 OK` si existe:

```json
{
  "id": 1,
  "amount": 1000,
  "phoneNumber": "3531234567",
  "operator": "PERSONAL",
  "type": "PREPAID",
  "status": "COMPLETED"
}
```

Respuesta `404 Not Found` si no existe.

### Reemplazar una recarga

```http
PUT /api/recharges/{id}
Content-Type: application/json

{
  "amount": 2000,
  "phoneNumber": "3531234567",
  "operator": "PERSONAL",
  "type": "PREPAID"
}
```

Respuesta `200 OK` si existe:

```json
{
  "id": 1,
  "amount": 2000,
  "phoneNumber": "3531234567",
  "operator": "PERSONAL",
  "type": "PREPAID",
  "status": "PENDING"
}
```

Respuesta `404 Not Found` si no existe.

### Eliminar una recarga

```http
DELETE /api/recharges/{id}
```

Respuesta `204 No Content` si existe.

Respuesta `404 Not Found` si no existe.

## Modelos

### `Operator`

```text
MOVISTAR
PERSONAL
CLARO
```

### `RechargeType`

```text
PREPAID
POSTPAID
```

### `RechargeStatus`

```text
PENDING
COMPLETED
FAILED
```
