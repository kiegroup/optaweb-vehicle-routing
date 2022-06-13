FROM docker.io/library/nginx:1.17.5
COPY nginx.conf /etc/nginx
COPY default.conf /tmp/default.template
ARG BACKEND_URL=http://backend:8080
RUN envsubst '${BACKEND_URL}' < /tmp/default.template > /etc/nginx/conf.d/default.conf \
        && rm /tmp/default.template \
# Make directories used by nginx owned and writable by the root group to support arbitrary user ID.
# See: https://docs.openshift.com/container-platform/4.2/openshift_images/create-images.html
        && chgrp 0 /var/cache/nginx/ \
        && chmod g=u /var/cache/nginx/ \
        && chgrp 0 /var/run/ \
        && chmod g=u /var/run/
COPY build /usr/share/nginx/html
EXPOSE 8080
