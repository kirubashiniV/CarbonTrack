@echo off
setlocal
set "BASE_DIR=%~dp0"
for /f "usebackq tokens=1,* delims==" %%A in (`findstr /b "distributionUrl=" "%BASE_DIR%.mvn\wrapper\maven-wrapper.properties"`) do set "DISTRIBUTION_URL=%%B"
if not defined DISTRIBUTION_URL (
    echo distributionUrl is missing from %BASE_DIR%.mvn\wrapper\maven-wrapper.properties 1>&2
    exit /b 1
)
for %%F in ("%DISTRIBUTION_URL%") do set "ARCHIVE_NAME=%%~nxF"
set "MAVEN_DIR=%ARCHIVE_NAME:~0,-8%"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\%MAVEN_DIR%"
if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
    powershell -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference = 'Stop'; $uri = [Uri]$env:DISTRIBUTION_URL; $cache = Split-Path $env:MAVEN_HOME -Parent; New-Item -ItemType Directory -Force -Path $cache | Out-Null; $archive = Join-Path $cache $uri.Segments[-1]; Invoke-WebRequest -Uri $uri -OutFile $archive; Expand-Archive -Path $archive -DestinationPath $cache -Force; Remove-Item -Path $archive -Force; if (-not (Test-Path (Join-Path $env:MAVEN_HOME 'bin\mvn.cmd'))) { throw 'Maven archive did not contain the expected executable.' }"
    if errorlevel 1 exit /b 1
)
call "%MAVEN_HOME%\bin\mvn.cmd" %*
exit /b %ERRORLEVEL%
