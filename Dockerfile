FROM maven:3.6.3-adoptopenjdk-11

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /app.jar

#ADD target/app.jar /app.jar
ENTRYPOINT exec java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 $JAVA_OPTS -jar /app.jar
