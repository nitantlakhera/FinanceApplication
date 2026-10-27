# Istio policies

- `mtls.yaml`: enforces STRICT mTLS for all workloads in the `finance` namespace.
- `telemetry.yaml`: enables Istio-generated Prometheus metrics.

Optional JWT authorization can be layered on top by adding a `RequestAuthentication`
resource pointing at the Keycloak realm JWKS endpoint and an `AuthorizationPolicy`
that requires a valid JWT on service routes.
