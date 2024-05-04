FROM eclipse-temurin:21
VOLUME /tmp
EXPOSE 8080
ARG JAR_FILE=target/phone-booking.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]