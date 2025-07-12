# ---------- BUILD STAGE ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests


# ---------- RUNTIME STAGE ----------
FROM openjdk:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/target/whatsapp-clone.jar ./app.jar

EXPOSE 7878

ENTRYPOINT ["java", "-jar", "app.jar"]
