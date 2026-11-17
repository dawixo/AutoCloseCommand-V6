@echo off
setlocal

rem Prefer configured JDK 25.
if defined JAVA_HOME set "PATH=%JAVA_HOME%\\bin;%PATH%"

where java >nul 2>nul
if errorlevel 1 (
  echo Nie znaleziono Javy. Zainstaluj JDK 25 i ustaw JAVA_HOME.
  pause
  exit /b 1
)

java -version
where gradle >nul 2>nul
if errorlevel 1 (
  echo Nie znaleziono Gradle. Zainstaluj Gradle 9.9.1 lub nowszy.
  pause
  exit /b 1
)

echo Budowanie AutoCloseCommand V6...
gradle build --no-configuration-cache
if errorlevel 1 (
  echo.
  echo Budowanie nie powiodlo sie. Sprawdz komunikaty powyzej.
  pause
  exit /b 1
)

echo.
echo GOTOWE: build\\libs\\autoclosecommand-1.1.0.jar
pause
