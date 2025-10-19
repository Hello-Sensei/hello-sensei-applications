FROM eclipse-temurin:21-jdk-alpine AS builder

# https://specs.opencontainers.org/image-spec/annotations/
LABEL "org.opencontainers.image.authors"="David Vera"
LABEL "org.opencontainers.image.created"="2025-10-19T15:00:00+01:00"
LABEL "org.opencontainers.image.authors"="david.vera@9online.fr"
LABEL "org.opencontainers.image.version"="0.0.1-SNAPSHOT"
LABEL "org.opencontainers.image.title"="Video uploader Microservice"
LABEL "org.opencontainers.image.description"="Microservice for managing Video uploads"

# Use adduser (lighter alternative to useradd)
RUN adduser --home /home/appuser --disabled-password appuser
USER appuser

WORKDIR /opt/app
# copy required files
COPY ./.mvn/ ./.mvn
COPY ./mvnw ./mvnw
COPY ./pom.xml ./pom.xml

RUN ./mvnw dependency:go-offline
COPY ./src ./src
RUN ./mvnw clean install -DskipTests


FROM eclipse-temurin:21-jre-alpine

# Use adduser (lighter alternative to useradd)
RUN adduser --home /home/appuser --disabled-password appuser
USER appuser

WORKDIR /opt/app
COPY --from=builder /opt/app/target/video-uploader.jar /opt/app/video-uploader.jar

ENTRYPOINT ["java", "-jar", "/opt/app/video-uploader.jar"]