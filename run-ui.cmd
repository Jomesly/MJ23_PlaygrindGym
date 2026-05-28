@echo off
setlocal

set "PROJECT_DIR=%~dp0"
set "M2=%USERPROFILE%\.m2\repository"
set "FX=%M2%\org\openjfx"
set "POI=%M2%\org\apache\poi"
set "APACHE=%M2%\org\apache"
set "COMMONS=%M2%\commons-io"

cd /d "%PROJECT_DIR%"

java --enable-native-access=javafx.graphics ^
  --module-path "%FX%\javafx-base\21.0.3\javafx-base-21.0.3-win.jar;%FX%\javafx-controls\21.0.3\javafx-controls-21.0.3-win.jar;%FX%\javafx-fxml\21.0.3\javafx-fxml-21.0.3-win.jar;%FX%\javafx-graphics\21.0.3\javafx-graphics-21.0.3-win.jar;%FX%\javafx-swing\21.0.3\javafx-swing-21.0.3-win.jar" ^
  --add-modules javafx.controls,javafx.fxml,javafx.swing ^
  -cp "target\classes;%M2%\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar;%M2%\mysql\mysql-connector-java\8.0.33\mysql-connector-java-8.0.33.jar;%M2%\com\zaxxer\HikariCP\5.1.0\HikariCP-5.1.0.jar;%M2%\org\slf4j\slf4j-api\2.0.9\slf4j-api-2.0.9.jar;%M2%\org\slf4j\slf4j-simple\2.0.9\slf4j-simple-2.0.9.jar;%POI%\poi-ooxml\5.2.3\poi-ooxml-5.2.3.jar;%POI%\poi\5.2.3\poi-5.2.3.jar;%POI%\poi-ooxml-lite\5.2.3\poi-ooxml-lite-5.2.3.jar;%APACHE%\xmlbeans\xmlbeans\5.1.1\xmlbeans-5.1.1.jar;%APACHE%\commons\commons-compress\1.21\commons-compress-1.21.jar;%COMMONS%\commons-io\2.11.0\commons-io-2.11.0.jar;%M2%\com\github\virtuald\curvesapi\1.07\curvesapi-1.07.jar;%APACHE%\commons\commons-collections4\4.4\commons-collections4-4.4.jar;%M2%\commons-codec\commons-codec\1.15\commons-codec-1.15.jar;%APACHE%\commons\commons-math3\3.6.1\commons-math3-3.6.1.jar;%M2%\com\zaxxer\SparseBitSet\1.2\SparseBitSet-1.2.jar;%APACHE%\logging\log4j\log4j-api\2.18.0\log4j-api-2.18.0.jar" ^
  mj23gym.ui.LoginScreen

endlocal
