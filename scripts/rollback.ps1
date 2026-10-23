# ==============================================================================
# Script: rollback.ps1
# Description: Automated Rollback & Recovery Script for Farmer Training Platform
# Usage: .\scripts\rollback.ps1 -Environment dev -Port 8085
# ==============================================================================

param(
    [string]$Environment = "dev",
    [int]$Port = 8085,
    [string]$DeployDir = "deploy",
    [string]$BackupDir = "backups"
)

Write-Host "======================================================================" -ForegroundColor Yellow
Write-Host ">>> [ROLLBACK] Initiating Automated Application Rollback & Recovery" -ForegroundColor Yellow
Write-Host ">>> [ROLLBACK] Target Environment : $Environment" -ForegroundColor Yellow
Write-Host ">>> [ROLLBACK] Target Port        : $Port" -ForegroundColor Yellow
Write-Host "======================================================================" -ForegroundColor Yellow

# 1. Stop current failing instance
$stopScript = Join-Path $PSScriptRoot "stop-app.ps1"
if (Test-Path $stopScript) {
    & $stopScript -Port $Port
}

# 2. Identify latest stable backup artifact
$latestBackup = Get-ChildItem -Path $BackupDir -Filter "*.jar" -ErrorAction SilentlyContinue |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $latestBackup) {
    Write-Host ">>> [ROLLBACK] No backup found in $BackupDir, falling back to target artifact..." -ForegroundColor Yellow
    $latestBackup = Get-Item "target/farmer-registration-platform-1.0.0.jar" -ErrorAction SilentlyContinue
}

if (-not $latestBackup -or -not (Test-Path $latestBackup.FullName)) {
    Write-Error ">>> [ROLLBACK ERROR] No stable backup artifact available for recovery!"
    exit 1
}

Write-Host ">>> [ROLLBACK] Restoring stable release from: $($latestBackup.FullName)" -ForegroundColor Green

# 3. Restore artifact to deploy directory
if (-not (Test-Path $DeployDir)) {
    New-Item -ItemType Directory -Path $DeployDir -Force | Out-Null
}
$targetJar = Join-Path $DeployDir "farmer-registration-platform-1.0.0.jar"
Copy-Item -Path $latestBackup.FullName -Destination $targetJar -Force

# 4. Redeploy restored artifact
$deployScript = Join-Path $PSScriptRoot "deploy.ps1"
& $deployScript -Environment $Environment -Port $Port -JarPath $targetJar -DeployDir $DeployDir

Write-Host "======================================================================" -ForegroundColor Green
Write-Host ">>> [ROLLBACK COMPLETED] Application successfully recovered and operational!" -ForegroundColor Green
Write-Host "======================================================================" -ForegroundColor Green
