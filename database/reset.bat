@echo off
setlocal

REM ====== CONFIGURE AQUI ======
set MYSQL_HOST=localhost
set MYSQL_PORT=3308
set MYSQL_USER=root
set MYSQL_PASSWORD=mysqlfatec
REM ============================

if "%MYSQL_PASSWORD%"=="" (
  echo [INFO] Rodando schema...
  mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% < "%~dp0schema.sql"
  if errorlevel 1 goto :error

  echo [INFO] Rodando seed...
  mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% < "%~dp0seed.sql"
  if errorlevel 1 goto :error
) else (
  echo [INFO] Rodando schema...
  mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "%~dp0schema.sql"
  if errorlevel 1 goto :error

  echo [INFO] Rodando seed...
  mysql -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER% -p%MYSQL_PASSWORD% < "%~dp0seed.sql"
  if errorlevel 1 goto :error
)

echo [OK] Banco resetado com sucesso.
exit /b 0

:error
echo [ERRO] Falhou. Confere usuario/senha e se o mysql.exe esta no PATH.
exit /b 1