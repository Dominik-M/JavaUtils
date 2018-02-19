set "APP_NAME=FileCopyR"

%JAVA_HOME%\javac -d bin src\main\*
cd bin
%JAVA_HOME%\jar cmf MANIFEST.MF ..\%APP_NAME%.jar *
cd ..
%JAVA_HOME%\java -jar %APP_NAME%.jar %1