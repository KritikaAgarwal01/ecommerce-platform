FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml .
COPY src/cart-service/pom.xml src/cart-service/pom.xml
COPY src/cart-service/src src/cart-service/src
RUN mvn -f src/cart-service/pom.xml -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -s /bin/sh -D appuser
COPY --from=build /workspace/src/cart-service/target/cart-service-1.0.0.jar app.jar
USER appuser
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "app.jar"]
