# Copyright 2019 Red Hat, Inc. and/or its affiliates.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

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
