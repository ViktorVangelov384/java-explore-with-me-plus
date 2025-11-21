FROM amazoncorretto:21
VOLUME /tmp
COPY target/*.jar main-service.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /main-service.jar"]

