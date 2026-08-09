FROM eclipse-temurin:25-jdk AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw && ./mvnw -q dependency:go-offline

COPY src src

RUN ./mvnw -q package -DskipTests && cp target/mdd-api-*.jar /workspace/mdd-api.jar

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /workspace/mdd-api.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
