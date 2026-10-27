# Finance Application — Technology Stack

This document lists every technology used to build, run, observe, secure, and deploy the Finance Application microservices.

---

## 1. Core Framework & Language

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Main programming language. |
| Maven | 3.x | Build tool and dependency management. |
| Spring Boot | 3.4.5 | Application framework for the microservices. |
| Spring Cloud BOM | 2024.0.1 | Cloud abstractions, including Spring Cloud LoadBalancer. |

---

## 2. Spring Ecosystem Libraries

| Library / Starter | Used In | Purpose |
|-------------------|---------|---------|
| `spring-boot-starter-web` | All service modules | REST API controllers and embedded Tomcat. |
| `spring-boot-starter-data-jpa` | user-service, payment-service, notification-service | PostgreSQL persistence. |
| `spring-boot-starter-data-mongodb` | order-service, inventory-service | MongoDB document persistence. |
| `spring-boot-starter-data-redis` | shared-common | Redis caching through Spring Cache. |
| `spring-boot-starter-oauth2-resource-server` | shared-common | JWT token validation / resource-server security. |
| `spring-boot-starter-security` | shared-common | Spring Security configuration. |
| `spring-boot-starter-actuator` | All service modules | Health, metrics, info, and `/actuator/prometheus` endpoints. |
| `spring-cloud-starter-loadbalancer` | shared-common | Client-side load balancing and `RestTemplate` support. |
| `spring-kafka` | order-service, payment-service, inventory-service, notification-service | Kafka event producers/consumers. |
| `springdoc-openapi-starter-webmvc-ui` | shared-common | Swagger UI / OpenAPI documentation. |
| `micrometer-registry-prometheus` | All service modules | Exposes metrics in Prometheus format at `/actuator/prometheus`. |
| `micrometer-tracing-bridge-brave` + `zipkin-reporter-brave` | All service modules | Distributed tracing via Micrometer Tracing and Brave; spans sent to Jaeger/Zipkin. |
| `spring-ai-bom` (`1.0.0-M6`) | ai-service | Spring AI core and chat/embedding/vector-store abstractions. |
| `spring-ai-openai-spring-boot-starter` | ai-service | Chat/embedding client for Kimi/Moonshot (OpenAI-compatible API). |
| `spring-ai-ollama-spring-boot-starter` | ai-service | Local LLM support via Ollama. |
| `spring-ai-pgvector-store-spring-boot-starter` | ai-service | RAG vector store backed by PostgreSQL pgvector. |
| `spring-ai-mcp-server-webmvc-spring-boot-starter` | ai-service | MCP server exposing `check_fraud_risk` tool. |

---

## 3. Service Discovery & Load Balancing

The project uses **Kubernetes-native service discovery** instead of a separate registry such as Eureka or Consul.

| Layer | Technology | Role |
|-------|------------|------|
| **Service registry / DNS** | Kubernetes `Service` objects | Each service gets a stable DNS name (`<service-name>.finance.svc.cluster.local`) and an `Endpoints` object maintained by Kubernetes. |
| **Endpoint distribution** | Kubernetes `EndpointSlices` | Efficient, scalable endpoint list distribution inside the cluster. |
| **Mesh-level discovery** | Istio + Envoy xDS/EDS | Istio Pilot watches Kubernetes endpoints and pushes them to Envoy sidecars so traffic can be routed directly to healthy pods. |
| **Client-side balancing** | Spring Cloud LoadBalancer | Used inside `ServiceClient` / `@LoadBalanced RestTemplate` for logical service IDs; falls back to simple static instances in tests. |
| **L7 mesh balancing** | Istio `DestinationRule` | Configures `LEAST_CONN`, connection pools, locality-aware routing, and outlier detection. |

---

## 4. Databases & Caching

| Store | Version / Image | Used By | Purpose |
|-------|-----------------|---------|---------|
| PostgreSQL | 16-alpine | user-service, payment-service, notification-service | Relational data store per service. |
| MongoDB | 7.0 | order-service, inventory-service | Document data store per service. |
| Redis | 7-alpine | All services via `shared-common` | Distributed read-through caching with Spring Cache. |
| PostgreSQL pgvector | pgvector/pgvector:pg16 | ai-service | Vector database for RAG document embeddings and knowledge base. |

---

## 5. Messaging

| Technology | Version / Image | Purpose |
|------------|-----------------|---------|
| Apache Kafka | 3.7.0 (Bitnami image) | Asynchronous event-driven communication between services. |
| Spring Kafka | Managed by Spring Boot BOM | Kafka producers, consumers, and listener containers. |

### Kafka topics used

- `orders.placed`
- `payments.processed`
- `inventory.reserved`

---

## 6. Security & Identity

| Technology | Version / Image | Purpose |
|------------|-----------------|---------|
| Keycloak | 24.0 | OIDC / JWT identity provider and token issuer. |
| Spring Security + OAuth2 Resource Server | Spring Boot managed | Validates JWT tokens and protects endpoints. |
| Istio mTLS | Latest Istio release | Mutual TLS for service-to-service encryption inside the mesh. |
| Istio `AuthorizationPolicy` | Latest Istio release | Fine-grained access control between services. |

---

## 7. API Gateway

| Gateway | Purpose |
|---------|---------|
| **Istio Gateway + VirtualService** (default) | Ingress traffic routing, retries, timeouts, and TLS termination. |
| **Kong** (provided as alternative) | API gateway with plugins; can be used instead of Istio ingress. |

---

## 8. Observability

| Signal | Tool | Version / Image | How it is used |
|--------|------|-----------------|----------------|
| Metrics | Prometheus | v2.52.0 | Scrapes `/actuator/prometheus` from each service. |
| Dashboards | Grafana | 10.4.2 | Visualizes Prometheus metrics. |
| Distributed tracing | Jaeger | 1.53 | Collects traces via Micrometer Tracing + Brave + Zipkin reporter. |
| Logs | Loki + Logback | Loki 2.9.10 | Centralized log aggregation (Logback emits application logs). |
| Mesh telemetry | Istio | Latest | Automatic Envoy metrics, access logs, and trace header propagation. |

---

## 9. Container & Orchestration Platform

| Technology | Version / Details | Purpose |
|------------|-------------------|---------|
| Docker | Latest stable | Container packaging for local development and image builds. |
| Kubernetes | v1.28+ assumed | Container orchestration, scheduling, scaling, and healing. |
| Istio | Latest stable | Service mesh providing mTLS, traffic management, and observability. |
| `finance` namespace | N/A | Logical isolation of all application components. |

### Kubernetes resources used

- `Namespace`, `Deployment`, `StatefulSet`, `Service`
- `ConfigMap`, `Secret`
- `HorizontalPodAutoscaler` (HPA)
- `PodDisruptionBudget` (PDB)
- Istio: `Gateway`, `VirtualService`, `DestinationRule`, `PeerAuthentication`, `Telemetry`, `AuthorizationPolicy`

---

## 10. Build & Packaging

| Tool / Script | Purpose |
|---------------|---------|
| `mvn clean package` | Builds all modules, runs unit tests, and produces executable JARs. |
| `spring-boot-maven-plugin` | Repackages JARs and enables layered Docker images. |
| `maven-compiler-plugin` 3.13.0 | Compiles Java 21 sources. |
| `maven-surefire-plugin` 3.2.5 | Runs unit tests (with ByteBuddy experimental flag for Java 21). |
| `build-images.sh` / `build-images.ps1` | Builds Docker images for all services (Bash or PowerShell). |

---

## 11. Testing Libraries

| Library | Purpose |
|---------|---------|
| JUnit 5 (Jupiter) | Unit and integration tests. |
| Spring Boot Test (`spring-boot-starter-test`) | Test slice annotations and `TestRestTemplate`. |
| Spring Kafka Test | Embedded Kafka for event-driven tests. |

---

## 12. Quick Reference: Per Service

| Service | Language / Framework | Database | Cache | Kafka Role |
|---------|----------------------|----------|-------|------------|
| user-service | Java 21 + Spring Boot | PostgreSQL | Redis | Not used |
| order-service | Java 21 + Spring Boot | MongoDB | Redis | Producer + consumer |
| payment-service | Java 21 + Spring Boot | PostgreSQL | Redis | Producer + consumer |
| inventory-service | Java 21 + Spring Boot | MongoDB | Redis | Producer + consumer |
| notification-service | Java 21 + Spring Boot | PostgreSQL | Redis | Consumer only |
| ai-service | Java 21 + Spring Boot + Spring AI | PostgreSQL pgvector | Redis | Not used |
| shared-common | Java 21 + Spring Boot | — | Redis configuration | Security, DTOs, load balancer utilities |

---

## 13. External Integrations (Conceptual)

The architecture diagram and documentation mention the following integrations that would be wired in a full production deployment but are not implemented in code yet:

- **Payment processor** (e.g., Stripe, PayPal) — invoked by `payment-service`.
- **Email / SMS / push notification provider** — invoked by `notification-service`.
- **AI model providers** — Kimi/Moonshot (OpenAI-compatible) or Ollama (local) invoked by `ai-service`.
- **External identity providers** — federated through Keycloak.

---

## 14. Summary Diagram

```text
┌─────────────────────────────────────────────────────────────────────┐
│                         Client / Load Balancer                       │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                    ┌──────────▼──────────┐
                    │ API Gateway         │
                    │ Istio Gateway / Kong│
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
        user-service    order-service     payment-service
              │                │                │
   PostgreSQL │         MongoDB │       PostgreSQL
   Redis      │          Redis  │       Redis
              │                │                │
        inventory-service  notification-service
              │                │
        MongoDB         PostgreSQL
        Redis           Redis
              │                │
              └────────┬───────┘
                       │
              Apache Kafka
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
   Keycloak      Prometheus      Jaeger / Grafana / Loki
   (OIDC/JWT)    + Grafana       (observability stack)
```

---

*Last updated: after implementing Kubernetes + Istio load balancing and service discovery.*

