#!/usr/bin/env bash
# ==============================================================================
# Script: docker-lifecycle.sh
# Description: Demonstrates complete Docker Container Lifecycle for Task 11
# Usage: ./scripts/docker-lifecycle.sh
# ==============================================================================

IMAGE_NAME="farmer-registration-platform"
TAG="1.0.0"
CONTAINER_NAME="farmer-platform-app"
HOST_PORT=8085
CONTAINER_PORT=8085

echo "======================================================================"
echo ">>> [DOCKER LIFECYCLE DEMONSTRATION - TASK 11]"
echo ">>> Target Image   : ${IMAGE_NAME}:${TAG}"
echo ">>> Container Name : ${CONTAINER_NAME}"
echo "======================================================================"

# 1. Cleanup
docker rm -f "${CONTAINER_NAME}" 2>/dev/null || true

# 2. Build
echo ">>> [STEP 2: BUILD] Building Docker image..."
docker build -t "${IMAGE_NAME}:${TAG}" -t "${IMAGE_NAME}:latest" .

# 3. Inspect
echo ">>> [STEP 3: INSPECT] Listing images..."
docker images "${IMAGE_NAME}"

# 4. Run
echo ">>> [STEP 4: RUN] Starting container..."
docker run -d --name "${CONTAINER_NAME}" -p "${HOST_PORT}:${CONTAINER_PORT}" -e SPRING_PROFILES_ACTIVE=dev "${IMAGE_NAME}:${TAG}"

# 5. Check
docker ps --filter "name=${CONTAINER_NAME}"

# 6. Logs
sleep 5
docker logs --tail 25 "${CONTAINER_NAME}"

# 7. Health Probe
echo ">>> [STEP 7: HEALTH PROBE] Probing http://localhost:${HOST_PORT}/actuator/health..."
for i in {1..15}; do
    sleep 2
    if curl -s "http://localhost:${HOST_PORT}/actuator/health" | grep -q '"status":"UP"'; then
        echo ">>> [HEALTHCHECK PASSED] Container is HEALTHY!"
        break
    fi
done

# 8. Stop & Restart
docker stop "${CONTAINER_NAME}"
docker start "${CONTAINER_NAME}"
echo ">>> [SUCCESS] Docker lifecycle completed."
