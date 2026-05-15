FROM maven:3.9.4-eclipse-temurin-17 AS builder
WORKDIR /workspace
ENV MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dhttps.protocols=TLSv1.2,TLSv1.3 -Dmaven.wagon.http.retryHandler.count=5"

COPY AIService/pom.xml AIService/pom.xml
COPY AIService/src AIService/src
RUN for i in 1 2 3; do mvn -f AIService/pom.xml clean package spring-boot:repackage -DskipTests -B && break || sleep 15; done

FROM bellsoft/liberica-openjre-debian:24.0.1
WORKDIR /application
RUN useradd -ms /bin/bash spring-user
USER spring-user
COPY --from=builder /workspace/AIService/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
