# Starts the Associate Registry Demo API and Angular app in two PowerShell windows.
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $MyInvocation.MyCommand.Path

$JavaHome = $env:JAVA_HOME
if ([string]::IsNullOrWhiteSpace($JavaHome)) {
    $JavaHome = "C:\Program Files\Semeru\jdk-25.0.2.10-openj9"
}
if (-not (Test-Path -LiteralPath (Join-Path $JavaHome "bin\java.exe"))) {
    Write-Error "Java was not found. Install a JDK 21 or newer and set JAVA_HOME. Looked in: $JavaHome"
}

$backendCommand = @"
`$env:JAVA_HOME = '$JavaHome'
`$env:Path = '$JavaHome\bin;' + `$env:Path
Set-Location -LiteralPath '$Root\backend'
Write-Host 'Associate Registry API: http://localhost:8080'
.\mvnw.cmd spring-boot:run
"@

$frontendCommand = @"
`$env:NG_CLI_ANALYTICS = 'false'
Set-Location -LiteralPath '$Root\frontend'
Write-Host 'Associate Registry UI: http://localhost:4200'
npm start
"@

Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit", "-Command", $backendCommand
Start-Process -FilePath "powershell.exe" -ArgumentList "-NoExit", "-Command", $frontendCommand

Write-Host "Opened two PowerShell windows."
Write-Host "API: http://localhost:8080"
Write-Host "UI:  http://localhost:4200"
Write-Host "The first backend start can take a minute while Maven finishes resolving dependencies."
Write-Host "Stop each window with Ctrl+C."
