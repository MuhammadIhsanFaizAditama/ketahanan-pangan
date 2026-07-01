@echo off
:: =====================================================================
:: run.bat — Skrip untuk menjalankan aplikasi SiPangan CLI
:: =====================================================================

set MYSQL_JAR=C:\Users\ASUS\Downloads\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar
set PDFBOX_JAR=lib\pdfbox-app-3.0.3.jar
set OUT_DIR=out

echo.
echo ============================================
echo   Menjalankan SiPangan CLI...
echo ============================================

java -cp "%OUT_DIR%;%MYSQL_JAR%;%PDFBOX_JAR%" sipangan.SiPanganApp

pause
