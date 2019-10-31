FROM docker.io/library/nginx
RUN rm /etc/nginx/conf.d/default.conf
COPY default.conf /tmp/default.template
COPY nginx.conf /etc/nginx
ARG BACKEND_URL
RUN envsubst '${BACKEND_URL}' < /tmp/default.template > /etc/nginx/conf.d/default.conf \
        && chmod -R 777 /var/log/nginx /var/cache/nginx/ \
        && chmod -R 777 /etc/nginx/ \
        && chmod 777 /var/run
COPY build /usr/share/nginx/html
EXPOSE 8080
