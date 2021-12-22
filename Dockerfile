FROM maven:3.6.3-adoptopenjdk-11

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

#ADD target/app.jar /app.jar

ENTRYPOINT exec java $JAVA_OPTS -jar /app.jar
