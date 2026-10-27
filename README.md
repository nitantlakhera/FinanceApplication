# Finance Application — Microservices Architecture

A cloud-native **Finance Application** built as a set of microservices using the exact stack shown in the architecture diagram.

![Architecture Diagram](./HighEndMicroservicedesign.png)

> For an editable, text-based version of this diagram — including every component block and a description of each concept — see [docs/Architecture.md](docs/Architecture.md).

## What This Project Demonstrates

- Microservices with **one database per service**
- **Redis** caching with Spring Cache abstraction
- **Istio service mesh** with Envoy sidecars
- **Kong / Envoy API Gateway**
- **Keycloak** OIDC/JWT authentication
- **Apache Kafka** event-driven communication
- **Observability** with Prometheus, Grafana, Jaeger, and Loki
- Deployment on **Kubernetes**

## Documentation

| Document | Purpose |
|---|---|
| [Tech Stack](docs/TechStack.md) | Complete inventory of languages, frameworks, stores, and tools |
| [Concepts](docs/Concepts.md) | Why each technology is used — for learners |
| [Architecture](docs/Architecture.md) | Requirements, architecture overview, flow, and component matrix |
| [Improvements](docs/Improvements.md) | Production-grade gap analysis and future enhancements |
| [Design](docs/Design.md) | Domain model, API endpoints, event schemas, and database design |
| [Installation Guide](docs/Installation-Guide.md) | Prerequisites, build steps, and Kubernetes deployment |
| [User Guide](docs/User-Guide.md) | How to use the application with example requests |
| [Local Docker Testing](docs/Local-Docker-Testing.md) | Test locally with Docker containers and `docker run` commands |

## Quick Start

1. Build all services:

   ```bash
   mvn clean package
   ```

   Once a service is running, explore its Swagger UI at `http://localhost:<port>/swagger-ui.html` and the raw OpenAPI spec at `/v3/api-docs`.

2. (Optional) Run locally with Docker Compose:

   ```bash
   docker-compose up -d
   mvn -f services/order-service/pom.xml spring-boot:run
   ```

   `docker-compose up -d` starts PostgreSQL, MongoDB, Redis, Kafka, and Jaeger.
   To exercise the full flow, start each service on its own port (e.g. 8081–8086) as described in [docs/Local-Docker-Testing.md](docs/Local-Docker-Testing.md).

3. Start the React frontend:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

   The frontend will be available at `http://localhost:5173` and proxies API calls to the backend services.

4. Build Docker images:

   ```bash
   ./build-images.sh
   ```

5. Deploy to Kubernetes:

   ```bash
   kubectl apply -f k8s/
   ```

6. Forward the gateway port:

   ```bash
   kubectl port-forward svc/istio-ingressgateway -n istio-system 8080:80
   ```

6. Open the app at `http://localhost:8080`.

The stack uses namespace `finance`. JWT is disabled by default; see the guides for enabling Keycloak authentication.

See the full [Installation Guide](docs/Installation-Guide.md) and [User Guide](docs/User-Guide.md) for details.

## Project Structure

```text
FinanceApplication/
├── README.md
├── docs/
│   ├── Architecture.md
│   ├── Architecture-Mermaid.png
│   ├── Architecture.mmd
│   ├── Concepts.md
│   ├── Design.md
│   ├── generate_architecture_png.py
│   ├── Improvements.md
│   ├── Installation-Guide.md
│   ├── Local-Docker-Testing.md
│   ├── TechStack.md
│   └── User-Guide.md
├── HighEndMicroservicedesign.png
├── generate_architecture_image.py
├── pom.xml
├── docker/
│   ├── postgres-init.sql
│   └── prometheus.yml
├── docker-compose.yml
├── build-images.sh
├── build-images.ps1
├── k8s/
├── frontend/
└── services/
    ├── shared-common/
    ├── user-service/
    ├── order-service/
    ├── payment-service/
    ├── inventory-service/
    └── notification-service/
```
