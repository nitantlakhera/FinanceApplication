# Finance Application — User Guide

This guide shows how to use the Finance Application once it is deployed.

---

## 1. Authenticate with Keycloak

Before calling business endpoints, you need a JWT token.

### 1.1 Port-Forward Keycloak

```bash
kubectl port-forward svc/keycloak -n finance 8180:8080
```

### 1.2 Get a Token

Use the demo user created during deployment.

```bash
TOKEN=$(curl -s -X POST \
  http://localhost:8180/realms/finance/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=finance-app' \
  -d 'username=demo' \
  -d 'password=demo' \
  | jq -r '.access_token')
```

If using Windows PowerShell without `jq`:

```powershell
$body = @{
  grant_type='password'
  client_id='finance-app'
  username='demo'
  password='demo'
}
$response = Invoke-RestMethod -Uri http://localhost:8180/realms/finance/protocol/openid-connect/token -Method Post -Body $body
$TOKEN = $response.access_token
```

Use `$TOKEN` in subsequent requests.

---

## 2. Port-Forward the API Gateway

If using the Istio / Envoy gateway:

```bash
kubectl port-forward svc/istio-ingressgateway -n istio-system 8080:80
```

The base URL is:

```text
http://localhost:8080
```

---

## 3. Create a User

```bash
curl -X POST http://localhost:8080/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "firstName": "Alice",
    "lastName": "Smith"
  }'
```

Save the returned `id` as `USER_ID`.

---

## 4. Add Inventory

```bash
curl -X POST http://localhost:8080/inventory \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD-1",
    "quantity": 100
  }'
```

Save the returned `productId` as `PRODUCT_ID`.

---

## 5. Place an Order

```bash
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"userId\": \"$USER_ID\",
    \"items\": [
      { \"productId\": \"PROD-1\", \"quantity\": 2 }
    ],
    \"totalAmount\": 199.99
  }")
```

Save the returned `id` as `ORDER_ID`.

---

## 6. Check Order Status

```bash
curl http://localhost:8080/orders/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN"
```

Expected flow:

1. Status is `PLACED` immediately after creation.
2. `payment-service` processes the payment and publishes `PaymentProcessedEvent`.
3. `inventory-service` reserves stock and publishes `InventoryReservedEvent`.
4. `order-service` updates the order to `COMPLETED`.

Wait a few seconds and call again to see the status change.

---

## 7. Check Payment

```bash
curl http://localhost:8080/payments/order/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN"
```

---

## 8. Check Inventory

```bash
curl http://localhost:8080/inventory/$PRODUCT_ID \
  -H "Authorization: Bearer $TOKEN"
```

You should see that `reservedQuantity` increased by the ordered amount.

---

## 9. Check Notifications

```bash
curl http://localhost:8080/notifications/order/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN"
```

You should see notifications for:

- Order placed
- Payment processed
- Inventory reserved

---

## 10. Explore Observability

### 10.1 Traces in Jaeger

```bash
kubectl port-forward svc/jaeger -n finance 16686:16686
```

Open `http://localhost:16686`, select a service such as `order-service`, and search for traces.

### 10.2 Metrics in Prometheus

```bash
kubectl port-forward svc/prometheus -n finance 9090:9090
```

Open `http://localhost:9090` and query metrics such as:

```text
http_server_requests_total
istio_requests_total
```

### 10.3 Dashboards in Grafana

```bash
kubectl port-forward svc/grafana -n finance 3000:3000
```

Open `http://localhost:3000` and browse the provisioned dashboards.

### 10.4 Logs in Grafana via Loki

In Grafana, go to **Explore**, select the **Loki** data source, and query:

```text
{app="order-service"}
```

---

## 11. Common Commands

| Task | Command |
|---|---|
| List pods | `kubectl get pods -n finance` |
| View service logs | `kubectl logs -n finance deployment/order-service` |
| Restart a service | `kubectl rollout restart deployment/order-service -n finance` |
| Open a shell in a pod | `kubectl exec -it -n finance deployment/order-service -- /bin/sh` |

> JWT is disabled by default (`app.security.enabled=false`). For local runs and the default Kubernetes deployment you can omit the `Authorization` header. Enable it only after configuring a Keycloak `finance` realm and setting `app.security.enabled=true`.
