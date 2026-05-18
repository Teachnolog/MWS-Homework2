# Homework-2nd-Sem
Домашние задания по Java, 2 семестр ВШПИ

## Homework 4: Resilient Secure HTTP Gateway

- Auth: `POST /api/v1/auth/login`
- Protected API: `GET /api/v1/profile`, `GET /api/v1/docs`, `POST/GET/DELETE /api/v1/tasks`
- External emulator: `/external/v1/tasks/**`, `/external/v1/unstable?mode=timeout|500|429|html`
- Observability: `X-Trace-Id`, access logs, `/actuator/health`, `/actuator/metrics`
- Resilience4j: `externalApi` rate limiter + circuit breaker with fallback in gateway service

Local integration tests
- Чтобы запустить интеграционные тесты локально (требуется Docker), выполните: `mvn test`. Тесты с Testcontainers будут выполнены автоматически, если Docker доступен; в противном случае они будут пропущены.
