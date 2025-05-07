# Etapa 1: Compilar el JAR con Maven (con Java 8)
FROM maven:3.8.8-eclipse-temurin-8 AS build

WORKDIR /app
COPY . .
COPY .m2 /root/.m2

RUN mvn clean package -DskipTests

# Etapa 2: Imagen final con Java 8
FROM eclipse-temurin:8-jre

WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8048
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
