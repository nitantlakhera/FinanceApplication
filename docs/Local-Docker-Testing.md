# Finance Application — Local Testing with Docker

This guide shows how to test the Finance Application locally using **plain `docker` commands**. It covers two common setups:

1. **Hybrid mode** — run the backing services (PostgreSQL, MongoDB, Kafka, Jaeger) in Docker, then run each Spring Boot service from Maven or your IDE.
2. **Full Docker mode** — build all service images and run every component as a Docker container.

> All security is disabled by default (`app.security.enabled=false`), so no JWT token is required.

---

## Prerequisites

- JDK 21 or later
- Maven 3.9+
- Docker Desktop or Docker Engine
- `curl` or any HTTP/REST client

---

## Option 1: Backing Services in Docker, Apps from Maven/IDE

This is the fastest way to debug individual services. The databases and message broker run in Docker while the Spring Boot services start on your machine on different ports.

### 1. Start the infrastructure

From the project root:

```bash
# Create a shared Docker network
docker network create finance-net

# PostgreSQL (ports 5432)
docker run -d --name postgres --network finance-net -p 5432:5432 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=userdb \
  -v "$(pwd)/docker/postgres-init.sql:/docker-entrypoint-initdb.d/postgres-init.sql:ro" \
  postgres:16

# MongoDB (port 27017)
docker run -d --name mongodb --network finance-net -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=root \
  -e MONGO_INITDB_ROOT_PASSWORD=root \
  mongo:7

# Redis (port 6379)
docker run -d --name redis --network finance-net -p 6379:6379 \
  redis:7-alpine

# Kafka 3.7 in KRaft mode (port 9092 on localhost)
docker run -d --name kafka --network finance-net -p 9092:9092 \
  -e KAFKA_CFG_NODE_ID=0 \
  -e KAFKA_KRAFT_CLUSTER_ID=abcdefghijklmnopqrstuv \
  -e KAFKA_CFG_PROCESS_ROLES=controller,broker \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@localhost:9093 \
  -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e ALLOW_PLAINTEXT_LISTENER=yes \
  bitnami/kafka:3.7

# Jaeger all-in-one (UI on 16686, Zipkin collector on 9411)
docker run -d --name jaeger --network finance-net \
  -p 16686:16686 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.55
```

> On Windows PowerShell replace `$(pwd)` with `${PWD}` and split the `\` continuations as needed.

Wait about 20–30 seconds for PostgreSQL to finish initializing the databases from `docker/postgres-init.sql`.

### 2. Start the Spring Boot services

Open a terminal for each service and run:

```bash
# user-service -> http://localhost:8081
SERVER_PORT=8081 mvn -f services/user-service/pom.xml spring-boot:run

# order-service -> http://localhost:8082
SERVER_PORT=8082 mvn -f services/order-service/pom.xml spring-boot:run

# payment-service -> http://localhost:8083
SERVER_PORT=8083 mvn -f services/payment-service/pom.xml spring-boot:run

# inventory-service -> http://localhost:8084
SERVER_PORT=8084 mvn -f services/inventory-service/pom.xml spring-boot:run

# notification-service -> http://localhost:8085
SERVER_PORT=8085 mvn -f services/notification-service/pom.xml spring-boot:run
```

> On Windows PowerShell use `$env:SERVER_PORT=8081` before each Maven command.

The default `application.yml` files already point to `localhost:5432`, `localhost:27017`, `localhost:9092`, and `localhost:9411`, so no extra environment variables are needed.

---

## Option 2: Everything in Docker

### 1. Build the images

```bash
./build-images.sh
```

> On Windows PowerShell use `.\build-images.ps1`.

### 2. Start the infrastructure

This time Kafka must advertise its Docker hostname so the service containers can reach it:

```bash
docker network create finance-net

docker run -d --name postgres --network finance-net -p 5432:5432 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=userdb \
  -v "$(pwd)/docker/postgres-init.sql:/docker-entrypoint-initdb.d/postgres-init.sql:ro" \
  postgres:16

docker run -d --name mongodb --network finance-net -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=root \
  -e MONGO_INITDB_ROOT_PASSWORD=root \
  mongo:7

docker run -d --name redis --network finance-net -p 6379:6379 \
  redis:7-alpine

docker run -d --name kafka --network finance-net -p 9092:9092 \
  -e KAFKA_CFG_NODE_ID=0 \
  -e KAFKA_KRAFT_CLUSTER_ID=abcdefghijklmnopqrstuv \
  -e KAFKA_CFG_PROCESS_ROLES=controller,broker \
  -e KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 \
  -e KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093 \
  -e KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e ALLOW_PLAINTEXT_LISTENER=yes \
  bitnami/kafka:3.7

docker run -d --name jaeger --network finance-net \
  -p 16686:16686 \
  -p 9411:9411 \
  jaegertracing/all-in-one:1.55
```


### 3. Start the application services

```bash
# user-service
docker run -d --name user-service --network finance-net -p 8081:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/userdb \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e SPRING_DATA_REDIS_HOST=redis \
  -e MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=http://jaeger:9411/api/v2/spans \
  -e APP_SECURITY_ENABLED=false \
  finance/user-service:1.0.0-SNAPSHOT

# order-service
docker run -d --name order-service --network finance-net -p 8082:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://root:root@mongodb:27017/orderdb?authSource=admin \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e SPRING_DATA_REDIS_HOST=redis \
  -e MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=http://jaeger:9411/api/v2/spans \
  -e APP_SECURITY_ENABLED=false \
  finance/order-service:1.0.0-SNAPSHOT

# payment-service
docker run -d --name payment-service --network finance-net -p 8083:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/paymentdb \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e SPRING_DATA_REDIS_HOST=redis \
  -e MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=http://jaeger:9411/api/v2/spans \
  -e APP_SECURITY_ENABLED=false \
  finance/payment-service:1.0.0-SNAPSHOT

# inventory-service
docker run -d --name inventory-service --network finance-net -p 8084:8080 \
  -e SPRING_DATA_MONGODB_URI=mongodb://root:root@mongodb:27017/inventorydb?authSource=admin \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e SPRING_DATA_REDIS_HOST=redis \
  -e MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=http://jaeger:9411/api/v2/spans \
  -e APP_SECURITY_ENABLED=false \
  finance/inventory-service:1.0.0-SNAPSHOT

# notification-service
docker run -d --name notification-service --network finance-net -p 8085:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/notificationdb \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  -e SPRING_DATA_REDIS_HOST=redis \
  -e MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=http://jaeger:9411/api/v2/spans \
  -e APP_SECURITY_ENABLED=false \
  finance/notification-service:1.0.0-SNAPSHOT
```


---

## End-to-End Test Flow

Once all services are healthy, run the following commands from a terminal.

```bash
# 1. Create a user
USER_RESPONSE=$(curl -s -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","firstName":"Alice","lastName":"Smith"}')
USER_ID=$(echo "$USER_RESPONSE" | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')
echo "USER_ID=$USER_ID"

# 2. Add inventory
curl -X POST http://localhost:8084/inventory \
  -H "Content-Type: application/json" \
  -d '{"productId":"PROD-1","availableQuantity":100}'

# 3. Place an order
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8082/orders \
  -H "Content-Type: application/json" \
  -d "{\"userId\":\"$USER_ID\",\"items\":[{\"productId\":\"PROD-1\",\"quantity\":2}],\"totalAmount\":199.99}")
ORDER_ID=$(echo "$ORDER_RESPONSE" | sed -n 's/.*"id":"\([^"]*\)".*/\1/p')
echo "ORDER_ID=$ORDER_ID"

# 4. Check order status (repeat after a few seconds)
curl -s http://localhost:8082/orders/$ORDER_ID | python -m json.tool

# 5. Check payment
curl -s http://localhost:8083/payments/order/$ORDER_ID | python -m json.tool

# 6. Check inventory
curl -s http://localhost:8084/inventory/PROD-1 | python -m json.tool

# 7. Check notifications
curl -s http://localhost:8085/notifications/order/$ORDER_ID | python -m json.tool
```

> On Windows use Git Bash or PowerShell with `Invoke-RestMethod`.

Expected outcome:

- Order status changes from `PLACED` → `PAYMENT_COMPLETED` → `INVENTORY_RESERVED` → `COMPLETED`.
- `payment-service` records a `SUCCESS` payment.
- `inventory-service` shows `reservedQuantity` increased by `2`.
- `notification-service` stores notifications for order placed, payment processed, and inventory reserved.


---

## Verify Health and Actuator Endpoints

Each service exposes Spring Boot Actuator on the same HTTP port:

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8081/actuator/health/liveness
curl http://localhost:8081/actuator/health/readiness
curl http://localhost:8081/actuator/prometheus
```

Replace `8081` with the port of the service you want to check.


---

## API Documentation / Swagger

Each service exposes a Swagger UI once it is running. Replace the port to match the service you want to explore:

- `http://localhost:8081/swagger-ui.html` — user-service
- `http://localhost:8082/swagger-ui.html` — order-service
- `http://localhost:8083/swagger-ui.html` — payment-service
- `http://localhost:8084/swagger-ui.html` — inventory-service
- `http://localhost:8085/swagger-ui.html` — notification-service

The OpenAPI JSON spec is available at `/v3/api-docs` on the same ports.

For the full list of endpoints and request/response schemas, also see [`Design.md`](./Design.md) and [`User-Guide.md`](./User-Guide.md).

---

## Verify Redis Caching

The services cache frequently read entities in Redis. With the infrastructure running, inspect cached entries:

```bash
# List known cache names and sample keys
docker exec -it redis redis-cli KEYS '*'

# Read a cached user (JSON payload stored by GenericJackson2JsonRedisSerializer)
docker exec -it redis redis-cli GET "users::<USER_ID>"

# Read a cached order
docker exec -it redis redis-cli GET "orders::<ORDER_ID>"
```

Repeating `GET /users/{id}` or `GET /orders/{id}` should return cached data without hitting PostgreSQL/MongoDB. You can confirm this by watching the service logs and the Prometheus `cache_gets_total` / `cache_misses_total` metrics exposed by Spring Boot.

---

## View Traces in Jaeger

Open [http://localhost:16686](http://localhost:16686), select a service such as `order-service`, and click **Find Traces**.


---

## Optional: Prometheus and Grafana

Each service already exposes metrics at `/actuator/prometheus`. You can run Prometheus and Grafana locally to see dashboards.

### 1. Start Prometheus

A sample configuration is in `docker/prometheus.yml`.

```bash
docker run -d --name prometheus --network finance-net -p 9090:9090 \
  -v "$(pwd)/docker/prometheus.yml:/etc/prometheus/prometheus.yml:ro" \
  prom/prometheus:v2.52.0
```

Open [http://localhost:9090](http://localhost:9090) and explore metrics.

### 2. Start Grafana

```bash
docker run -d --name grafana --network finance-net -p 3000:3000 \
  -e GF_SECURITY_ADMIN_USER=admin \
  -e GF_SECURITY_ADMIN_PASSWORD=admin \
  -e GF_USERS_ALLOW_SIGN_UP=false \
  grafana/grafana:10.4.2
```

Open [http://localhost:3000](http://localhost:3000), sign in with `admin/admin`, then add a Prometheus data source pointing to `http://prometheus:9090`.

> For Option 2 (full Docker), edit `docker/prometheus.yml` so targets point to the service container names, e.g. `user-service:8080` instead of `host.docker.internal:8081`.

---

## Cleanup

### Option 1 cleanup

```bash
# Stop infrastructure
docker stop postgres mongodb redis kafka jaeger keycloak prometheus grafana
docker rm postgres mongodb redis kafka jaeger keycloak prometheus grafana

# Remove network
docker network rm finance-net
```

Stop each Maven process with `Ctrl+C`.

### Option 2 cleanup

```bash
docker stop user-service order-service payment-service inventory-service notification-service kafka mongodb redis postgres jaeger keycloak prometheus grafana
docker rm user-service order-service payment-service inventory-service notification-service kafka mongodb redis postgres jaeger keycloak prometheus grafana
docker network rm finance-net
```


---

## Optional: Enabling Keycloak (JWT Security)

Local testing runs with `app.security.enabled=false` by default, so no token is required. To test the JWT flow, start Keycloak and enable the security property.

### 1. Run Keycloak locally

```bash
docker run -d --name keycloak --network finance-net -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -e KC_PROXY=edge \
  quay.io/keycloak/keycloak:24.0 start-dev
```

Wait ~30 seconds, then open the admin console at [http://localhost:8080/admin](http://localhost:8080/admin) and sign in with `admin/admin`.

### 2. Configure a realm and client

1. Create a realm named `finance`.
2. Create an **openid-connect** client, e.g. `finance-app`.
   - Enable **Client authentication**.
   - Set **Valid redirect URIs** to `*` for local testing.
   - In the **Credentials** tab, copy the **Client secret**.
3. Create a user, set a password, and add a role (e.g. `user`) to the user.

### 3. Enable security on the services

- For Maven/IDE runs add the environment variables below before `spring-boot:run`:

  ```bash
  export APP_SECURITY_ENABLED=true
  export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://localhost:8080/realms/finance
  ```

- For Docker containers add the following to each `docker run` command (services reach Keycloak by container name inside the Docker network):

  ```bash
  -e APP_SECURITY_ENABLED=true \
  -e SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://keycloak:8080/realms/finance
  ```

### 4. Get a token and call the API

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/realms/finance/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=finance-app" \
  -d "client_secret=<CLIENT_SECRET>" \
  -d "username=<USER_NAME>" \
  -d "password=<PASSWORD>" \
  | grep -o '"access_token":"[^"]*"' | cut -d'"' -f4)

curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/users
```

> On Windows use Git Bash or a similar shell for the token extraction, or copy the `access_token` manually from the JSON response.

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| Service fails to connect to database | PostgreSQL still initializing | Wait 20–30 seconds after starting Postgres before launching services. |
| Kafka consumer cannot connect | Wrong advertised listener | For host services use `PLAINTEXT://localhost:9092`; for Docker-only use `PLAINTEXT://kafka:9092`. |
| Ports already in use | Another process is using 5432, 27017, 9092, 8080 (Keycloak), 8081–8086, or 16686 | Stop the conflicting process or remap the host ports. |
| `curl` returns empty | Service still starting | Check logs with `docker logs <container>` or look at the Maven console. |
| Order stays `PLACED` | Kafka consumers are not running | Ensure `payment-service`, `inventory-service`, and `notification-service` are started and connected to Kafka. |

---

## Next Steps

- For Kubernetes deployment instructions, see [`Installation-Guide.md`](./Installation-Guide.md).
- For API details and event schemas, see [`Design.md`](./Design.md) and [`User-Guide.md`](./User-Guide.md).

