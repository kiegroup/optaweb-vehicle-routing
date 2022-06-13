FROM docker.io/adoptopenjdk/openjdk15:ubi-minimal-jre
ENV APP_ROUTING_ENGINE air
COPY target/*-exec.jar /opt/app/optaweb-vehicle-routing.jar
WORKDIR /opt/app
VOLUME /opt/app/local
CMD ["java", "-jar", "optaweb-vehicle-routing.jar"]
EXPOSE 8080
