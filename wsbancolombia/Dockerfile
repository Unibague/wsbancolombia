# Etapa 1: Compilar el JAR con Maven
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Copiar todo el proyecto al contenedor
COPY . /app
WORKDIR /app

# Compilar el proyecto sin ejecutar los tests
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final con solo el JAR y lo necesario
FROM eclipse-temurin:21-jre

# Crear carpetas necesarias
RUN mkdir /app && mkdir /app/certs

# Copiar el JAR generado desde la etapa anterior
COPY --from=build /app/target/rinWS7.jar /app/app.jar

# (Opcional) Copiar certificados si los tienes en una carpeta local
COPY certs/*.jks /app/certs/

# Exponer el puerto (ajústalo si tu app usa otro)
EXPOSE 8048

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
