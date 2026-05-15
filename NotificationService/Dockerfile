FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace
ENV MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true"

COPY Core/pom.xml Core/pom.xml
COPY Core/src Core/src
RUN mvn -f Core/pom.xml clean install -DskipTests -B

COPY NotificationService/pom.xml NotificationService/pom.xml
COPY NotificationService/src NotificationService/src
RUN mvn -f NotificationService/pom.xml clean package spring-boot:repackage -DskipTests -B

FROM bellsoft/liberica-openjre-debian:24.0.1
WORKDIR /application
RUN useradd -ms /bin/bash spring-user
USER spring-user
COPY --from=builder /workspace/NotificationService/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
