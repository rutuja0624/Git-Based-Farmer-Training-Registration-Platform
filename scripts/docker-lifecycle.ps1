# ==============================================================================
# Script: docker-lifecycle.ps1
# Description: Demonstrates complete Docker Container Lifecycle for Task 11
# Usage: .\scripts\docker-lifecycle.ps1
# ==============================================================================

param(
    [string]$ImageName = "farmer-registration-platform",
    [string]$Tag = "1.0.0",
    [string]$ContainerName = "farmer-platform-app",
    [int]$HostPort = 8085,
    [int]$ContainerPort = 8085
)

$FullImage = "$ImageName`:$Tag"

Write-Host "======================================================================" -ForegroundColor Cyan
Write-Host ">>> [DOCKER LIFECYCLE DEMONSTRATION - TASK 11]" -ForegroundColor Cyan
Write-Host ">>> Target Image     : $FullImage" -ForegroundColor Cyan
Write-Host ">>> Container Name   : $ContainerName" -ForegroundColor Cyan
Write-Host ">>> Port Mapping     : ${HostPort}:${ContainerPort}" -ForegroundColor Cyan
Write-Host "======================================================================" -ForegroundColor Cyan

# 1. Clean previous container if running
Write-Host "`n>>> [STEP 1: CLEANUP] Removing any existing container '$ContainerName'..." -ForegroundColor Yellow
docker rm -f $ContainerName 2>$null | Out-Null

# 2. Build and Tag Image
Write-Host "`n>>> [STEP 2: BUILD] Building Docker Image: $FullImage..." -ForegroundColor Cyan
docker build -t $FullImage -t "$ImageName`:latest" .
if ($LASTEXITCODE -ne 0) {
    Write-Error "Docker build failed!"
    exit 1
}

# 3. Inspect Image Details
Write-Host "`n>>> [STEP 3: INSPECT IMAGE] Inspecting created Docker image..." -ForegroundColor Green
docker images $ImageName

# 4. Run Container
Write-Host "`n>>> [STEP 4: RUN] Starting container '$ContainerName' on port $HostPort..." -ForegroundColor Cyan
docker run -d `
    --name $ContainerName `
    -p "${HostPort}:${ContainerPort}" `
    -e SPRING_PROFILES_ACTIVE=dev `
    $FullImage

# 5. Check Container Status
Write-Host "`n>>> [STEP 5: PS] Verifying running container status..." -ForegroundColor Green
docker ps --filter "name=$ContainerName"

# 6. Stream Container Logs
Write-Host "`n>>> [STEP 6: LOGS] Fetching initial container stdout logs..." -ForegroundColor Cyan
Start-Sleep -Seconds 5
docker logs --tail 25 $ContainerName

# 7. Health Probe Verification
Write-Host "`n>>> [STEP 7: HEALTH PROBE] Testing application endpoint on http://localhost:$HostPort/actuator/health..." -ForegroundColor Yellow
$healthy = $false
for ($i = 1; $i -le 15; $i++) {
    Start-Sleep -Seconds 2
    try {
        $res = Invoke-RestMethod -Uri "http://localhost:$HostPort/actuator/health" -TimeoutSec 3 -ErrorAction Stop
        if ($res.status -eq "UP") {
            $healthy = $true
            Write-Host ">>> [HEALTHCHECK PASSED] Container is HEALTHY! Status: $($res.status)" -ForegroundColor Green
            break
        }
    } catch {
        Write-Host ">>> Waiting for container startup... ($i/15)" -ForegroundColor DarkGray
    }
}

if (-not $healthy) {
    Write-Warning ">>> [HEALTHCHECK TIMEOUT] Container did not report healthy within timeout."
}

# 8. Stop Container
Write-Host "`n>>> [STEP 8: STOP] Demonstrating graceful container stop..." -ForegroundColor Yellow
docker stop $ContainerName

# 9. Restart Container
Write-Host "`n>>> [STEP 9: RESTART] Restarting container..." -ForegroundColor Cyan
docker start $ContainerName
docker ps --filter "name=$ContainerName"

Write-Host "`n======================================================================" -ForegroundColor Green
Write-Host ">>> [DOCKER LIFECYCLE COMPLETED SUCCESSFULLY]" -ForegroundColor Green
Write-Host ">>> Image: $FullImage | Container: $ContainerName" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Green
