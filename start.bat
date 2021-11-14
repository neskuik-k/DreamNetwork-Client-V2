@echo off
"C:\Program Files\Java\jdk-16.0.2\bin\java" -Debug=true --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.ref=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED -jar .\target\DreamNetworkV2-1.7.0-SNAPSHOT-withShadedDependencies.jar
PAUSE