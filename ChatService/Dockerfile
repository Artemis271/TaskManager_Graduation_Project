FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY Core/pom.xml Core/pom.xml
COPY Core/src Core/src
RUN mvn -f Core/pom.xml clean install -DskipTests -B

COPY ChatService/pom.xml ChatService/pom.xml
COPY ChatService/src ChatService/src
RUN mvn -f ChatService/pom.xml clean package spring-boot:repackage -DskipTests -B

FROM bellsoft/liberica-openjre-debian:24.0.1
WORKDIR /application
RUN useradd -ms /bin/bash spring-user
USER spring-user
COPY --from=builder /workspace/ChatService/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]