@echo off
:: =====================================================================
:: compile.bat — Skrip kompilasi otomatis untuk proyek SiPangan
::
:: CARA PAKAI:
::   1. Edit baris JAVAFX_PATH dan MYSQL_JAR sesuai lokasi di PC Anda
::   2. Klik dua kali compile.bat atau jalankan di Command Prompt
:: =====================================================================

:: ⚙️ SESUAIKAN PATH INI DENGAN LOKASI DI KOMPUTER ANDA:
set JAVAFX_PATH=C:\Users\ASUS\Downloads\openjfx-26.0.1_windows-x64_bin-sdk\javafx-sdk-26.0.1\lib
set MYSQL_JAR=C:\Users\ASUS\Downloads\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0\mysql-connector-j-9.7.0.jar

:: Library folder (Apache PDFBox untuk cetak PDF)
set PDFBOX_JAR=lib\pdfbox-app-3.0.3.jar

:: Folder output hasil kompilasi (.class files)
set OUT_DIR=out

:: Buat folder output jika belum ada
if not exist %OUT_DIR% mkdir %OUT_DIR%

echo.
echo ============================================
echo   Kompilasi SiPangan...
echo ============================================

javac ^
  --module-path "%JAVAFX_PATH%" ^
  --add-modules javafx.controls ^
  -cp ".;%MYSQL_JAR%;%PDFBOX_JAR%" ^
  -d %OUT_DIR% ^
  src\main\java\sipangan\model\Notifikasi.java ^
  src\main\java\sipangan\model\Person.java ^
  src\main\java\sipangan\model\User.java ^
  src\main\java\sipangan\model\ProdukPangan.java ^
  src\main\java\sipangan\model\Stok.java ^
  src\main\java\sipangan\db\DatabaseConnection.java ^
  src\main\java\sipangan\db\SiPanganDAO.java ^
  src\main\java\sipangan\SiPanganApp.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Kompilasi BERHASIL! File .class tersimpan di folder: %OUT_DIR%
    echo.
    echo Jalankan dengan: run.bat
) else (
    echo.
    echo ❌ Kompilasi GAGAL! Periksa pesan error di atas.
)
pause
