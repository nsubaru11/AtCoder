@echo off
chcp 65001 > nul

set "SERVER_PATH=C:\Users\20051\Projects\WebStorm\AtCoder_Scripts\EasyTestv2\local-runner-server.js"

echo ========================================
echo   Local Runner Server for Java 24
echo ========================================
echo.
echo JAVA_HOME_24: %JAVA_HOME_24%
echo.

if not defined JAVA_HOME_24 (
    echo [ERROR] JAVA_HOME_24 is not set.
    pause
    exit /b 1
)

"%JAVA_HOME_24%\bin\java" -version
echo.

echo Starting server...
node "%SERVER_PATH%"

pause