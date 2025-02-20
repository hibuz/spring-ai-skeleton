FROM amazoncorretto:17-alpine
LABEL org.opencontainers.image.authors="hibuz@hibuz.com"
LABEL org.opencontainers.image.source="https://github.com/hibuz/spring-ai-skeleton"

VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
ARG JAR_FILE=build/libs/*-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar
