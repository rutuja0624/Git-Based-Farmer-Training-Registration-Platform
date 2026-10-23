#!/usr/bin/env bash
# ==============================================================================
# Script: stop-app.sh
# Description: Stops running application on target port
# Usage: ./scripts/stop-app.sh 8085
# ==============================================================================

PORT="${1:-8085}"
echo ">>> [STOP] Checking for processes on port $PORT..."
PID=$(lsof -ti :$PORT 2>/dev/null || fuser $PORT/tcp 2>/dev/null || true)
if [ -n "$PID" ]; then
    echo ">>> [STOP] Killing process $PID..."
    kill -9 $PID 2>/dev/null || true
    echo ">>> [STOP] Port $PORT cleared."
else
    echo ">>> [STOP] No processes listening on port $PORT."
fi
