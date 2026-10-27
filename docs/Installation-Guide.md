# Finance Application — Installation Guide

This guide explains how to build the application and deploy it on a local Kubernetes cluster.

---

## 1. Prerequisites

Install the following tools on your laptop:

| Tool | Purpose | Suggested Version |
|---|---|---|
| JDK | Compile Java services | 21 or later |
| Maven | Build Java services | 3.9.x |
| Docker | Build container images | Latest stable |
| Kubernetes | Container orchestration | Docker Desktop K8s / minikube / kind |
| kubectl | Interact with Kubernetes | Latest stable |
| Istio | Service mesh | Latest stable |
| Helm (optional) | Install observability tools | Latest stable |

### 1.1 Install Istio

```bash
istioctl install --set profile=demo -y
kubectl label namespace default istio-injection=enabled
```

### 1.2 Enable Istio Sidecar Injection

```bash
kubectl create namespace finance
kubectl label namespace finance istio-injection=enabled
```

---

## 2. Build the Java Services

From the project root:

```bash
mvn clean package
```

To build a single service and its dependencies:

```bash
mvn clean package -pl user-service -am
```

This creates a Spring Boot jar for each service under `services/<service-name>/target/`.

---

## 2.5. Local Development Without Kubernetes

You can start the backing infrastructure locally with Docker Compose:

```bash
docker-compose up -d
```

This starts PostgreSQL, MongoDB, Redis, Kafka (KRaft), and Jaeger.

Then start each service from its module directory or from the IDE on the default
port `8080`.  Because every service currently defaults to `app.security.enabled=false`,
no JWT token is required for local calls.


---

## 3. Build Docker Images

### 3.1 Using the Provided Script

```bash
# Linux / macOS
./build-images.sh

# Windows PowerShell
.\build-images.ps1
```

### 3.2 Manual Build

Build each service image:

```bash
docker build -t finance/user-service:1.0.0-SNAPSHOT -f services/user-service/Dockerfile services/user-service
docker build -t finance/order-service:1.0.0-SNAPSHOT -f services/order-service/Dockerfile services/order-service
docker build -t finance/payment-service:1.0.0-SNAPSHOT -f services/payment-service/Dockerfile services/payment-service
docker build -t finance/inventory-service:1.0.0-SNAPSHOT -f services/inventory-service/Dockerfile services/inventory-service
docker build -t finance/notification-service:1.0.0-SNAPSHOT -f services/notification-service/Dockerfile services/notification-service
```

If using minikube or kind, make sure the images are available inside the cluster. For minikube:

```bash
eval $(minikube docker-env)
```

Then rebuild the images.

---

## 4. Deploy to Kubernetes

Apply the manifests in order. They are numbered in the `k8s/` directory.

```bash
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-databases/
kubectl apply -f k8s/02-kafka/
kubectl apply -f k8s/03-keycloak/
kubectl apply -f k8s/04-services/
kubectl apply -f k8s/05-istio/
kubectl apply -f k8s/06-gateway/
kubectl apply -f k8s/07-observability/
```

Redis is included in `k8s/01-databases/redis.yaml`; services connect to it at `redis:6379` and use it as the Spring Cache provider.

Wait for all pods to be ready:

```bash
kubectl get pods -n finance -w
```

---

## 5. Access the Application

### 5.1 Port-Forward the API Gateway

If using the **Istio / Envoy gateway**:

```bash
kubectl port-forward svc/istio-ingressgateway -n istio-system 8080:80
```

If using the **Kong gateway**:

```bash
kubectl port-forward svc/kong -n finance 8080:80
```

The application is now reachable at `http://localhost:8080`.

### 5.2 Access Keycloak

```bash
kubectl port-forward svc/keycloak -n finance 8180:8080
```

Open `http://localhost:8180` and log in with the default admin credentials (`admin` / `admin`) defined in `k8s/03-keycloak/keycloak.yaml`.

> If you enable JWT (`app.security.enabled=true`), create a `finance` realm, a `finance-app` OIDC client, and a demo user in Keycloak. The application expects the issuer `http://keycloak:8080/realms/finance`.

---

## 6. Access Observability Tools

### Prometheus

```bash
kubectl port-forward svc/prometheus -n finance 9090:9090
```

Open `http://localhost:9090`.

### Grafana

```bash
kubectl port-forward svc/grafana -n finance 3000:3000
```

Open `http://localhost:3000` and log in with the default credentials (usually `admin/admin`).

### Jaeger

```bash
kubectl port-forward svc/jaeger -n finance 16686:16686
```

Open `http://localhost:16686`.

### Loki / Grafana Logs

Loki is usually accessed through Grafana using the Loki data source.


### Kubernetes Dashboard

The Kubernetes Dashboard is not deployed by this project, but if you have it installed in your cluster you can access it with:

```bash
kubectl proxy
```

Then open [http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/](http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/).

Create a token if required:

```bash
kubectl -n kubernetes-dashboard create token admin-user
```

---

## 7. Verify the Deployment

Check that all services are healthy:

```bash
kubectl get pods -n finance
kubectl get svc -n finance
```

Check service mesh injection:

```bash
kubectl get pods -n finance -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.spec.containers[*].name}{"\n"}{end}'
```

Each application pod should show two containers: your service and the `istio-proxy` sidecar.

---

## 8. Cleanup

To remove everything:

```bash
kubectl delete -f k8s/
```

To remove Istio:

```bash
istioctl uninstall --purge -y
```
