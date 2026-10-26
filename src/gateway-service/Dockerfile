FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY src/gateway-service/pom.xml src/gateway-service/pom.xml
COPY src/gateway-service/src src/gateway-service/src
RUN mvn -f src/gateway-service/pom.xml -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -s /bin/sh -D appuser
COPY --from=build /workspace/src/gateway-service/target/gateway-service-1.0.0.jar app.jar
USER appuser
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
