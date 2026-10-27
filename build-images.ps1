$VERSION = "1.0.0-SNAPSHOT"

mvn clean package -DskipTests

$services = @("user-service", "order-service", "payment-service", "inventory-service", "notification-service")
foreach ($svc in $services) {
    Write-Host "Building finance/${svc}:${VERSION} ..."
    docker build -t "finance/${svc}:${VERSION}" -f "services/${svc}/Dockerfile" "services/${svc}"
}

Write-Host "All images built."
