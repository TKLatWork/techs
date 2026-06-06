$ErrorActionPreference = "Stop"

Write-Host "=== Building Java modules ===" -ForegroundColor Cyan
mvn clean install
if ($LASTEXITCODE -ne 0) {
    Write-Error "Maven build failed"
    exit $LASTEXITCODE
}

Write-Host "`n=== Building web module ===" -ForegroundColor Cyan
npm --workspace web run build
if ($LASTEXITCODE -ne 0) {
    Write-Error "Web build failed"
    exit $LASTEXITCODE
}

Write-Host "`n=== Full build successful ===" -ForegroundColor Green
