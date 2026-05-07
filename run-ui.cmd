@echo off
setlocal

set "PROJECT_DIR=%~dp0"
set "M2=%USERPROFILE%\.m2\repository"
set "FX=%M2%\org\openjfx"

cd /d "%PROJECT_DIR%"

java --enable-native-access=javafx.graphics ^
  --module-path "%FX%\javafx-base\21.0.3\javafx-base-21.0.3-win.jar;%FX%\javafx-controls\21.0.3\javafx-controls-21.0.3-win.jar;%FX%\javafx-fxml\21.0.3\javafx-fxml-21.0.3-win.jar;%FX%\javafx-graphics\21.0.3\javafx-graphics-21.0.3-win.jar;%FX%\javafx-swing\21.0.3\javafx-swing-21.0.3-win.jar" ^
  --add-modules javafx.controls,javafx.fxml,javafx.swing ^
  -cp "target\classes;%M2%\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar;%M2%\mysql\mysql-connector-java\8.0.33\mysql-connector-java-8.0.33.jar;%M2%\com\zaxxer\HikariCP\5.1.0\HikariCP-5.1.0.jar;%M2%\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar;%M2%\org\slf4j\slf4j-simple\2.0.9\slf4j-simple-2.0.9.jar" ^
  mj23gym.ui.GymManagementApp

endlocal
