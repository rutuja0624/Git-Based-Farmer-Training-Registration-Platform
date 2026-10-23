#!/usr/bin/env bash
# ==============================================================================
# Script: deploy.sh
# Description: Automated server deployment script (Unix/Linux/macOS)
# Usage: ./scripts/deploy.sh dev 8085
# ==============================================================================

ENV="${1:-dev}"
PORT="${2:-8085}"
JAR_PATH="${3:-target/farmer-registration-platform-1.0.0.jar}"
DEPLOY_DIR="${4:-deploy}"

echo "======================================================================"
echo ">>> [DEPLOY] Target Environment : $ENV"
echo ">>> [DEPLOY] Target HTTP Port   : $PORT"
echo ">>> [DEPLOY] Source Artifact    : $JAR_PATH"
echo "======================================================================"

if [ ! -f "$JAR_PATH" ]; then
    echo ">>> [ERROR] JAR artifact $JAR_PATH not found!"
    exit 1
fi

# Stop existing process on port
PID=$(lsof -ti :$PORT 2>/dev/null || fuser $PORT/tcp 2>/dev/null || true)
if [ -n "$PID" ]; then
    echo ">>> [DEPLOY] Stopping process $PID on port $PORT..."
    kill -9 $PID 2>/dev/null || true
    sleep 2
fi

mkdir -p "$DEPLOY_DIR" logs
TARGET_JAR="$DEPLOY_DIR/$(basename $JAR_PATH)"
cp "$JAR_PATH" "$TARGET_JAR"

nohup java -Dspring.profiles.active="$ENV" -Dserver.port="$PORT" -jar "$TARGET_JAR" > logs/app-stdout.log 2> logs/app-stderr.log &
echo ">>> [DEPLOY] Application started in background."

HEALTH_URL="http://localhost:$PORT/actuator/health"
echo ">>> [DEPLOY] Checking health at $HEALTH_URL..."

for i in {1..20}; do
    sleep 2
    if curl -s "$HEALTH_URL" | grep -q '"status":"UP"'; then
        echo ">>> [DEPLOY SUCCESS] Application is HEALTHY on port $PORT!"
        echo ">>> [SMOKE TEST] Verifying /api/trainings..."
        curl -s "http://localhost:$PORT/api/trainings" | grep -q 'title' && echo ">>> [SMOKE TEST] API catalog responded OK"
        echo ">>> [SMOKE TEST] Verifying Portal Homepage..."
        curl -s -o /dev/null -w "%{http_code}" "http://localhost:$PORT/" | grep -q '200' && echo ">>> [SMOKE TEST] Portal UI responded HTTP 200 OK"
        exit 0
    fi
    echo ">>> Waiting for server startup... ($i/20)"
done

echo ">>> [DEPLOY FAILED] Application failed to report healthy."
exit 1
