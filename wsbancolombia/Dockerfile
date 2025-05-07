# Etapa 1: Compilar el JAR con Maven (con Java 8)
FROM maven:3.8.8-eclipse-temurin-8 AS build

WORKDIR /app
COPY . .

# Instalar ojdbc8.jar en el repositorio local del contenedor
RUN mvn install:install-file \
  -Dfile=libs/ojdbc8.jar \
  -DgroupId=com.oracle \
  -DartifactId=ojdbc8 \
  -Dversion=19.3 \
  -Dpackaging=jar

RUN mvn clean package -DskipTests

# Etapa 2: Imagen final con Java 8
FROM eclipse-temurin:8-jre

WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8048
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
