# API

Documento vivo de endpoints y contratos de la API.

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
