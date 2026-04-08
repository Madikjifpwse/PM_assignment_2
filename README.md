# Resilient Food Delivery Service (Console Prototype)

## Key Features (The "Big Four")

1. **Stateful Workflow**: Orders follow a strict lifecycle: `CREATED` -> `CONFIRMED` -> `DELIVERED`. Illegal transitions (e.g., CREATED directly to DELIVERED) are blocked by internal logic.
2. **Simple Caching**: Implements a "Cache-Aside" pattern using a `HashMap`. The system tracks Cache Hits/Misses to demonstrate performance optimization.
3. **File-Based Persistence**: All data is persisted in `data/orders.json`. The system automatically recovers its state after a crash.
4. **Logging & Security**: Every action is audited in `logs/log.txt` with nanosecond precision timestamps.

## CIA Triad Implementation
- **Confidentiality**: Access control logic separation between User, Restaurant, and Courier roles (simulated).
- **Integrity**: Enforced by immutable state transitions and validation logic in the `Order` model.
- **Availability**: Resilience through local file persistence and automatic crash recovery.

## Technology Stack
- **Language**: Java 21
- **Dependency Management**: Maven
- **Libraries**: Google Gson (JSON Serialization)
- **Architecture**: Layered Architecture (Model, Repository, Cache, Service, UI)
