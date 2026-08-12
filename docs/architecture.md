# Arquitectura

Documento vivo para registrar la arquitectura de PayFlow.

## Modelo de capas

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Storage / Database
```

## Responsabilidades

| Capa | Responsabilidad |
|------|-----------------|
| Controller | Recibir requests HTTP, interpretar parámetros, delegar al Service, devolver responses. |
| Service | Contener la lógica de negocio y coordinar Repositories. |
| Repository | Acceder a datos. Actualmente en memoria; más adelante JPA/PostgreSQL. |
| Domain | Modelos del negocio: `Recharge`, `Operator`, `RechargeType`, `RechargeStatus`. |
| DTO | Representar datos que entran o salen de la API. |

## Inyección de dependencias

Spring conecta las capas mediante constructor injection:

```text
RechargeController
    ↓
RechargeService
    ↓
RechargeRepository
```

Cada componente es un Bean administrado por el ApplicationContext.

## Estado actual

- `PayflowController`: endpoints de health check y status.
- `RechargeController`: `POST /api/recharges`, `GET /api/recharges/{id}`.
- `RechargeService`: validaciones básicas y orquestación.
- `RechargeRepository`: almacenamiento en memoria con `ConcurrentHashMap`.
- DTOs: `HelloResponse`, `StatusResponse`, `CreateRechargeRequest`, `RechargeResponse`.

## Decisiones técnicas

- Uso de `BigDecimal` para montos de dinero.
- Uso de `enum class` para valores cerrados (`Operator`, `RechargeType`, `RechargeStatus`).
- Constructor injection en todos los componentes.
- `ConcurrentHashMap` y `AtomicLong` para simular persistencia de forma thread-safe.
