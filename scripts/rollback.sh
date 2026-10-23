#!/usr/bin/env bash
# ==============================================================================
# Script: rollback.sh
# Description: Automated Rollback & Recovery Script (Linux/Unix)
# Usage: ./scripts/rollback.sh dev 8085
# ==============================================================================

ENV="${1:-dev}"
PORT="${2:-8085}"
BACKUP_DIR="backups"
DEPLOY_DIR="deploy"

echo "======================================================================"
echo ">>> [ROLLBACK] Initiating Automated Application Rollback"
echo ">>> [ROLLBACK] Target Environment : $ENV"
echo ">>> [ROLLBACK] Target Port        : $PORT"
echo "======================================================================"

./scripts/stop-app.sh "$PORT"

BACKUP_JAR=$(ls -t "$BACKUP_DIR"/*.jar 2>/dev/null | head -n 1)
if [ -z "$BACKUP_JAR" ]; then
    BACKUP_JAR="target/farmer-registration-platform-1.0.0.jar"
fi

if [ ! -f "$BACKUP_JAR" ]; then
    echo ">>> [ERROR] No backup artifact found!"
    exit 1
fi

echo ">>> [ROLLBACK] Restoring stable release from $BACKUP_JAR..."
mkdir -p "$DEPLOY_DIR"
cp "$BACKUP_JAR" "$DEPLOY_DIR/farmer-registration-platform-1.0.0.jar"

./scripts/deploy.sh "$ENV" "$PORT" "$DEPLOY_DIR/farmer-registration-platform-1.0.0.jar" "$DEPLOY_DIR"
echo ">>> [ROLLBACK COMPLETED] Application recovered successfully."
