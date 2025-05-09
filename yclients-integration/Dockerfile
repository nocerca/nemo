# Модуль yclients-integration
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Копируем JAR и ресурсы (включая миграции Liquibase)
COPY target/yclients-integration-*.jar app.jar
COPY src/main/resources/db/changelog/ ./db/changelog/

ENTRYPOINT ["java", "-jar", "app.jar"]