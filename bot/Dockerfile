# Модуль bot
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/bot-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]