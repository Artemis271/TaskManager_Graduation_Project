FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY ProjectService/pom.xml ProjectService/pom.xml
COPY ProjectService/src ProjectService/src
RUN mvn -f ProjectService/pom.xml clean package spring-boot:repackage -DskipTests -B

FROM bellsoft/liberica-openjre-debian:24.0.1
WORKDIR /application
RUN useradd -ms /bin/bash spring-user
USER spring-user
COPY --from=builder /workspace/ProjectService/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]