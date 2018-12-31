FROM openjdk:8-jre-alpine

RUN mkdir -p /coffee

ADD ./build/libs/* /coffee/

WORKDIR /coffee

EXPOSE 7766
EXPOSE 7755

CMD ["java", "-jar", "Coffee-1.0-SNAPSHOT.jar", "-Dcom.sun.management.jmxremote.port=8899", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "-Dcom.sun.management.jmxremote.rmi.port=8899", "-Djava.rmi.server.hostname=$HOST_HOSTNAME"]
