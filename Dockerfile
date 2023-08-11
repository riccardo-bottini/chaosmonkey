FROM openjdk:17
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
ARG JAR_FILE
ADD target/${JAR_FILE} app.jar