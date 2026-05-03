FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -q -DskipTests -pl auth-service -am package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/auth-service/target/auth-service-1.0.0.jar app.jar
EXPOSE 8081
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]