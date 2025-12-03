@echo off
chcp 65001 > nul

set "SERVER_PATH=./local-runner-server.js"

REM デフォルトは24、引数があればそれを使用
if "%~1"=="" (
    set "JAVA_VER=24"
) else (
    set "JAVA_VER=%~1"
)

REM 対応するJAVA_HOME変数名を構築
set "JAVA_HOME_VAR=JAVA_HOME_%JAVA_VER%"

REM 環境変数の値を取得
call set "JAVA_PATH=%%%JAVA_HOME_VAR%%%"

echo ========================================
echo   Local Runner Server for Java %JAVA_VER%
echo ========================================
echo %JAVA_HOME_VAR%: %JAVA_PATH%

if "%JAVA_PATH%"=="" (
    echo [ERROR] %JAVA_HOME_VAR% is not set.
    pause
    exit /b 1
)

"%JAVA_PATH%\bin\java" -version

set "JAVA_HOME=%JAVA_PATH%"

echo Starting server...
node "%SERVER_PATH%"

pause