# Finance Application — Concepts

This document explains **why** each major technology in the architecture is used. It is meant for learners who want to understand the reasoning behind the design, not just the implementation.

---

## 1. Why Kubernetes?

**Kubernetes** is a container orchestration platform. A microservices system has many applications, databases, message queues, gateways, and observability tools running at the same time.

Kubernetes helps by:

- **Scheduling containers** on available machines.
- **Restarting failed pods** automatically.
- **Scaling services** up or down based on demand.
- **Managing configuration** with ConfigMaps and Secrets.
- **Load balancing** traffic across multiple replicas of a service.

It provides the runtime foundation that everything else is built on.

---

## 2. Why Istio Service Mesh?

In a microservices architecture, services talk to each other constantly. Without a mesh, every service would have to implement:

- Encryption
- Retries and timeouts
- Load balancing
- Access control
- Observability

**Istio** adds a **service mesh** by injecting a proxy (Envoy) next to each application pod. This sidecar intercepts all network traffic and handles those concerns for you.

Istio provides:

- **mTLS**: encrypts and authenticates traffic between services.
- **Traffic management**: routing, retries, timeouts, canary releases.
- **Security policies**: allow or deny traffic between specific services.
- **Automatic telemetry**: metrics, logs, and traces without changing application code.

The application code stays focused on business logic.

---

## 3. Why Envoy?

**Envoy** is a high-performance, programmable proxy. Istio uses Envoy as its **data plane**.

The data plane is the part of the mesh that actually routes and secures network traffic. Each pod gets an Envoy sidecar that intercepts all incoming and outgoing traffic and applies the policies defined in Istio.

Envoy is fast, lightweight, and widely used in production service meshes.

---

## 4. Why Kong / Envoy as API Gateway?

The **API Gateway** is the single entry point for external clients. Instead of exposing every microservice directly to the internet, clients call the gateway, and the gateway forwards requests to the right service.

This pattern provides:

- **Centralized authentication and rate limiting**
- **SSL termination**
- **Request routing and versioning**
- **Hiding internal service details**

In this project:

- **Kong** is a popular gateway with a rich plugin ecosystem.
- **Envoy** is the native Istio gateway data plane.

We use **Envoy via Istio Gateway as the default**, and provide a **Kong DB-less alternative**.

---

## 5. Why Keycloak?

**Keycloak** is an open-source identity and access management server.

It handles:

- **Authentication**: verifying who a user is.
- **Authorization**: deciding what the user is allowed to do.
- **OIDC / OAuth2 / JWT**: industry-standard security protocols.

When a user logs in, Keycloak issues a **JWT token**. The API Gateway and Istio can validate this token before allowing the request into the system.

This keeps security centralized instead of duplicating auth logic in every service.

---

## 6. Why Apache Kafka?

Some operations should not be synchronous. When an order is placed, payment and inventory processing should happen reliably, but the order service should not wait for both.

**Apache Kafka** is a distributed event streaming platform. Services publish **events** to Kafka, and other services subscribe to those events.

Kafka provides:

- **Decoupling**: producers and consumers do not need to know about each other.
- **Scalability**: handles high throughput and many consumers.
- **Reliability**: messages are durable and can be replayed.
- **Resilience**: if a consumer is down, messages wait in Kafka until it recovers.

---

## 7. Why Redis?

As services grow, repeatedly reading the same database rows adds load and latency. **Redis** is an in-memory data store that is ideal for low-latency caching.

In this project, Spring Cache is backed by Redis, giving:

- **Faster reads**: frequently requested entities (users, orders, inventory, payments, notifications) are served from memory.
- **Less database load**: cache hits avoid trips to PostgreSQL or MongoDB.
- **Simple invalidation**: write operations evict stale cache entries so clients do not see old data.
- **Shared cache**: all services can use the same Redis instance, while still keeping their own databases.

---

## 8. Why One Database per Service?

In a microservices architecture, each service should own its own data. This avoids tight coupling through a shared database schema.

Benefits:

- **Independence**: each service can choose the database that fits its data model.
- **Isolation**: changes in one service do not accidentally break another.
- **Scalability**: each database can be scaled independently.

In this project:

- **PostgreSQL** is used for structured, relational data: users, payments, notifications.
- **MongoDB** is used for flexible, document-style data: orders, inventory.

---

## 9. Why Prometheus, Grafana, Jaeger, and Loki?

When many services work together, debugging becomes difficult. Observability tools answer three important questions:

- **Metrics (Prometheus + Grafana)**: Is the system healthy? What are the request rates, latencies, and error rates?
- **Traces (Jaeger)**: Where is time spent across services for one request?
- **Logs (Loki)**: What happened at a specific point in time?

Istio also contributes mesh-level metrics and traces automatically through Envoy sidecars.

---

## 10. Summary

| Component | Purpose |
|---|---|
| Kubernetes | Run and manage containers |
| Istio | Secure and manage service-to-service traffic |
| Envoy | Proxy that implements Istio policies |
| Kong / Envoy Gateway | Single entry point for external clients |
| Keycloak | Identity and JWT token management |
| Apache Kafka | Asynchronous event streaming |
| Redis | Shared in-memory cache |
| PostgreSQL / MongoDB | One database per service |
| Prometheus + Grafana | Metrics and dashboards |
| Jaeger | Distributed tracing |
| Loki | Centralized logging |
