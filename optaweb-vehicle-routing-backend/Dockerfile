FROM fabric8/java-alpine-openjdk8-jre
ENV APP_ROUTING_ENGINE air
COPY target/*-exec.jar /deployments
EXPOSE 8080
VOLUME /deployments/local
