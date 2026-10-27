#!/bin/bash
set -e

VERSION="1.0.0-SNAPSHOT"

mvn clean package -DskipTests

for svc in user-service order-service payment-service inventory-service notification-service; do
  echo "Building finance/${svc}:${VERSION} ..."
  docker build -t "finance/${svc}:${VERSION}" -f "services/${svc}/Dockerfile" "services/${svc}"
done

echo "All images built."
