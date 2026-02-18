# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/whatsapp-clone.jar ./app.jar

EXPOSE 7878

ENTRYPOINT ["java", "-jar", "app.jar"]
