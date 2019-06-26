FROM fabric8/java-centos-openjdk8-jdk

ENV SPRING_APPLICATION_JSON {\"app\": {\"routing\": {\"osm-file\": \"boston.osm.pbf\"},	\"region\": {\"country-codes\": \"US\"}}}

ADD https://docs.google.com/uc?export=download&id=1zfCar7alWFvaZewm60Upy7E8gyxmfLlN /deployments/local/openstreetmap/boston.osm.pbf
ADD https://docs.google.com/uc?export=download&id=1zsm9L7zfTRqvddaKliPHVRML1GMfD5pE /deployments/local/data/hosipitals_in_massachusetts_65.yaml
ADD https://docs.google.com/uc?export=download&id=1370BKmkiaK9NA642elgBg--q45rbF7PG /deployments/local/data/atms_in_boston_40.yaml


COPY target/*.jar /deployments

USER root
RUN chown -R jboss:jboss /deployments/local && \
    chmod -R 777 /deployments/local && \
    chmod 777 /deployments/local/openstreetmap/ && \
    chmod 444 /deployments/local/openstreetmap/boston.osm.pbf && \
    chmod 666 /deployments/local/data/hosipitals_in_massachusetts_65.yaml && \
    chmod 666 /deployments/local/data/atms_in_boston_40.yaml

USER jboss

EXPOSE 8080