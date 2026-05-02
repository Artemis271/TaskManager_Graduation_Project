FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY TaskService/pom.xml TaskService/pom.xml
COPY TaskService/src TaskService/src
RUN mvn -f TaskService/pom.xml clean package spring-boot:repackage -DskipTests -B

FROM bellsoft/liberica-openjre-debian:24.0.1
WORKDIR /application
RUN useradd -ms /bin/bash spring-user
USER spring-user
COPY --from=builder /workspace/TaskService/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]