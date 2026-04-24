FROM maven:3.9.5-eclipse-temurin-17 AS build 
WORKDIR /app 
COPY . . 
RUN mvn clean package -DskipTests 
 
FROM eclipse-temurin:17-jre-alpine 
WORKDIR /app 
COPY --from=build /app/target/fluxia-1.0.0.jar app.jar 
EXPOSE 8080 
ENTRYPOINT ["java", "-jar", "app.jar"] 
