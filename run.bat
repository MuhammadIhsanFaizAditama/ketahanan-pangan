@echo off
:: =====================================================================
:: run.bat — Skrip untuk menjalankan aplikasi SiPangan
::
:: PASTIKAN compile.bat sudah dijalankan terlebih dahulu!
:: =====================================================================

set JAVAFX_PATH=C:\Users\ASUS\Downloads\openjfx-26.0.1_windows-x64_bin-sdk\javafx-sdk-26.0.1\lib
set MYSQL_JAR=C:\Users\ASUS\Downloads\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar

:: Library folder (Apache PDFBox untuk cetak PDF)
set PDFBOX_JAR=lib\pdfbox-app-3.0.3.jar

set OUT_DIR=out

echo.
echo ============================================
echo   Menjalankan SiPangan...
echo ============================================

java ^
  --module-path "%JAVAFX_PATH%" ^
  --add-modules javafx.controls ^
  -cp "%OUT_DIR%;%MYSQL_JAR%;%PDFBOX_JAR%" ^
  sipangan.SiPanganApp

pause
