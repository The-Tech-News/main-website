# Stage 1: Build with maven
FROM maven:3.9.12-eclipse-temurin-21-alpine AS maven-build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B package --file pom.xml

# Stage 2: Deploy with Tomcat
FROM dhi.io/tomcat:10-jdk21
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=maven-build /app/target/main-proj-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
