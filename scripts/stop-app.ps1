# ==============================================================================
# Script: stop-app.ps1
# Description: Gracefully stops any application running on the specified target port
# Usage: .\scripts\stop-app.ps1 -Port 8085
# ==============================================================================

param(
    [int]$Port = 8085
)

Write-Host ">>> [STOP] Checking for active processes listening on port $Port..." -ForegroundColor Cyan

$connections = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue

if ($connections) {
    $processIds = $connections | Select-Object -ExpandProperty OwningProcess -Unique
    foreach ($pidToKill in $processIds) {
        if ($pidToKill -gt 0) {
            $processName = (Get-Process -Id $pidToKill -ErrorAction SilentlyContinue).ProcessName
            Write-Host ">>> [STOP] Found process '$processName' (PID: $pidToKill) on port $Port. Terminating..." -ForegroundColor Yellow
            Stop-Process -Id $pidToKill -Force -ErrorAction SilentlyContinue
            Start-Sleep -Seconds 2
        }
    }
    Write-Host ">>> [STOP] Port $Port is now clear." -ForegroundColor Green
} else {
    Write-Host ">>> [STOP] No running processes found on port $Port." -ForegroundColor Green
}
