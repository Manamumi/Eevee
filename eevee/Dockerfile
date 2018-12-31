FROM openjdk:8-jre-alpine

ENV GOOGLE_APPLICATION_CREDENTIALS="conf/Eevee.Google.json"
ENV COFFEE_HOST="192.168.1.16"

RUN mkdir -p /eevee

ADD ./build/libs/* /eevee/

ADD .BUILD_INFO /eevee/.BUILD_INFO

WORKDIR /eevee

EXPOSE 8899

CMD ["java", "-jar", "Eevee-1.0-SNAPSHOT.jar", "-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"]