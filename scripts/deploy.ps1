# ==============================================================================
# Script: deploy.ps1
# Description: Automated server deployment script for Farmer Training Platform
# Usage: .\scripts\deploy.ps1 -Environment dev -Port 8085
# ==============================================================================

param(
    [string]$Environment = "dev",
    [int]$Port = 8085,
    [string]$JarPath = "target/farmer-registration-platform-1.0.0.jar",
    [string]$DeployDir = "deploy"
)

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host ">>> [DEPLOY] Initiating Automated Application Server Deployment" -ForegroundColor Cyan
Write-Host ">>> [DEPLOY] Target Environment : $Environment" -ForegroundColor Cyan
Write-Host ">>> [DEPLOY] Target HTTP Port   : $Port" -ForegroundColor Cyan
Write-Host ">>> [DEPLOY] Source Artifact    : $JarPath" -ForegroundColor Cyan
Write-Host ">>> [DEPLOY] Deployment Folder  : $DeployDir" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

# 1. Validate Artifact
if (-not (Test-Path $JarPath)) {
    Write-Error ">>> [DEPLOY ERROR] Artifact not found at $JarPath! Build the package before deploying."
    exit 1
}

# 2. Stop Any Conflicting Process
$stopScript = Join-Path $PSScriptRoot "stop-app.ps1"
if (Test-Path $stopScript) {
    & $stopScript -Port $Port
}

# 3. Prepare Deployment Directory
if (-not (Test-Path $DeployDir)) {
    New-Item -ItemType Directory -Path $DeployDir -Force | Out-Null
}

$targetJar = Join-Path $DeployDir (Split-Path $JarPath -Leaf)
Copy-Item -Path $JarPath -Destination $targetJar -Force
Write-Host ">>> [DEPLOY] Copied artifact to $targetJar" -ForegroundColor Green

# 4. Prepare Logs Directory
$logsDir = "logs"
if (-not (Test-Path $logsDir)) {
    New-Item -ItemType Directory -Path $logsDir -Force | Out-Null
}
$stdoutLog = Join-Path $logsDir "app-stdout.log"
$stderrLog = Join-Path $logsDir "app-stderr.log"

# 5. Launch Process in Background
$jvmArgs = "-Dspring.profiles.active=$Environment -Dserver.port=$Port -jar `"$targetJar`""
Write-Host ">>> [DEPLOY] Launching application with JVM args: $jvmArgs" -ForegroundColor Cyan

$javaExe = if (Get-Command javaw -ErrorAction SilentlyContinue) { "javaw" } else { "java" }
$process = Start-Process -FilePath $javaExe `
    -ArgumentList "-Dspring.profiles.active=$Environment", "-Dserver.port=$Port", "-jar", "`"$targetJar`"" `
    -RedirectStandardOutput $stdoutLog `
    -RedirectStandardError $stderrLog `
    -WindowStyle Hidden `
    -PassThru

Write-Host ">>> [DEPLOY] Process started with PID: $($process.Id)" -ForegroundColor Green

# 6. Health Check Probe
$healthUrl = "http://localhost:$Port/actuator/health"
Write-Host ">>> [DEPLOY] Waiting for application to initialize and report UP at $healthUrl..." -ForegroundColor Yellow

$retries = 20
$healthy = $false

for ($i = 1; $i -le $retries; $i++) {
    Start-Sleep -Seconds 2
    try {
        $response = Invoke-RestMethod -Uri $healthUrl -Method Get -TimeoutSec 3 -ErrorAction Stop
        if ($response.status -eq "UP") {
            $healthy = $true
            break
        }
    } catch {
        Write-Host ">>> [DEPLOY] Probe attempt $i/$retries... waiting for server initialization" -ForegroundColor DarkGray
    }
}

if ($healthy) {
    Write-Host "======================================================================" -ForegroundColor Green
    Write-Host ">>> [DEPLOY SUCCESS] Application is HEALTHY and listening on port $Port!" -ForegroundColor Green
    Write-Host ">>> [DEPLOY SUCCESS] Portal URL   : http://localhost:$Port/" -ForegroundColor Green
    Write-Host ">>> [DEPLOY SUCCESS] Health Check : http://localhost:$Port/actuator/health" -ForegroundColor Green
    Write-Host ">>> [DEPLOY SUCCESS] Active Env   : $Environment" -ForegroundColor Green

    # Automated Smoke Tests
    Write-Host ">>> [SMOKE TEST] Querying /api/trainings endpoint..." -ForegroundColor Cyan
    try {
        $trainings = Invoke-RestMethod -Uri "http://localhost:$Port/api/trainings" -Method Get -TimeoutSec 3
        Write-Host ">>> [SMOKE TEST SUCCESS] Catalog returned $($trainings.Count) training programs." -ForegroundColor Green
    } catch {
        Write-Host ">>> [SMOKE TEST WARNING] Failed to retrieve trainings catalog: $_" -ForegroundColor Yellow
    }

    Write-Host ">>> [SMOKE TEST] Querying Web Portal UI at http://localhost:$Port/..." -ForegroundColor Cyan
    try {
        $page = Invoke-WebRequest -Uri "http://localhost:$Port/" -Method Get -TimeoutSec 3
        Write-Host ">>> [SMOKE TEST SUCCESS] Web Portal responded with HTTP $($page.StatusCode)." -ForegroundColor Green
    } catch {
        Write-Host ">>> [SMOKE TEST WARNING] Web Portal response error: $_" -ForegroundColor Yellow
    }
    Write-Host "======================================================================" -ForegroundColor Green
    exit 0
} else {
    Write-Host "======================================================================" -ForegroundColor Red
    Write-Host ">>> [DEPLOY FAILED] Application failed to report healthy within timeout." -ForegroundColor Red
    if (Test-Path $stderrLog) {
        Write-Host ">>> Application Error Log:" -ForegroundColor Red
        Get-Content -Path $stderrLog -Tail 20
    }
    Write-Host "======================================================================" -ForegroundColor Red
    exit 1
}
