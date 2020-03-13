#!/usr/bin/env bash
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

set -e

readonly script_name="./$(basename "$0")"

function print_help() {
  echo "Usage:"
  echo "  $script_name [OSM_FILE_NAME COUNTRY_CODE_LIST OSM_FILE_DOWNLOAD_URL]"
  echo "  $script_name --air"
  echo "  $script_name --help"
  echo
  echo "First form configures backend to use GraphHopper routing mode and downloads an OSM data file during startup. \
Note that download and processing of the OSM file can take some time depending on its size. \
During this period, the application informs about backend service being unreachable."
  echo
  echo "Second form configures backend to use air routing mode. This is useful for development, debugging and \
hacking. Air distance routing is only an approximation. It is not useful for real vehicle routing."
  echo
  echo
  echo "OSM_FILE_NAME"
  echo "  The file downloaded from OSM_FILE_DOWNLOAD_URL will be saved under this name."
  echo
  echo "COUNTRY_CODE_LIST"
  echo "  ISO_3166-1 country code used to filter geosearch results. You can provide multiple, comma-separated values."
  echo
  echo "OSM_FILE_DOWNLOAD_URL"
  echo "  Should point to an OSM data file in PBF format accessible from OpenShift. The file will be downloaded \
during backend startup and saved as /deployments/local/OSM_FILE_NAME."
  echo
  echo
  echo "Example 1"
  echo "  $script_name belgium-latest.osm.pbf BE http://download.geofabrik.de/europe/belgium-latest.osm.pbf"
  echo
  echo "  Configures the application to filter geosearch results to Belgium and download the latest Belgium \
OSM extract from Geofabrik."
  echo
  echo
  echo "Example 2"
  echo "  $script_name my-city.osm.pbf FR https://download.bbbike.org/osm/extract/planet_12.032,53.0171_12.1024,53.0491.osm.pbf"
  echo
  echo "  Configures the application to download a custom region defined using the BBBike service and save it \
as my-city.osm.pbf."
}

function wrong_args() {
    print_help
    echo >&2
    echo >&2 "ERROR: Wrong arguments."
    exit 1
}

[[ $1 == "--help" ]] && print_help && exit 0

# Process arguments
declare -a dc_backend_env
dc_backend_env+=("SPRING_PROFILES_ACTIVE=production")
case $# in
  0)
    print_help
    exit 0
    ;;
  1)
    if [[ $1 == --air ]]
    then
      dc_backend_env+=("APP_ROUTING_ENGINE=air")
      summary="No routing config provided. The backend will start in air distance mode.\n\n\
WARNING: Air distance mode does not give accurate values. \
It is only useful for evaluation, debugging, or incremental setup purpose. \
You can run ‘$script_name --help’ to see other options."
    else
      wrong_args
    fi
    ;;
  2)
    dc_backend_env+=("APP_ROUTING_ENGINE=air")
    dc_backend_env+=("APP_ROUTING_OSM_FILE=$1")
    dc_backend_env+=("APP_REGION_COUNTRY_CODES=$2")
    summary="The backend will start in air mode. Use the backend pod to upload a graph directory or an OSM file. \
Then change routing mode to graphopper. Run ‘$script_name --help’ for more info."
    ;;
  3)
    dc_backend_env+=("APP_ROUTING_ENGINE=graphhopper")
    dc_backend_env+=("APP_ROUTING_OSM_FILE=$1")
    dc_backend_env+=("APP_REGION_COUNTRY_CODES=$2")
    dc_backend_env+=("APP_ROUTING_OSM_DOWNLOAD_URL=$3")
    summary="The backend will download an OSM file on startup. \
It may take several minutes to download and process the file before the application is fully available!"
    download=1
    ;;
  *)
    wrong_args
esac

# Change dir to the project root (where the script is located) to correctly resolve module paths.
# This is needed in case the script was called from a different location than the project root.
cd "$(dirname "$(readlink -f "$0")")"

readonly dir_backend=optaweb-vehicle-routing-backend
readonly dir_frontend=optaweb-vehicle-routing-frontend

# Fail fast if the project hasn't been built
if ! stat -t ${dir_backend}/target/*.jar > /dev/null 2>&1
then
  echo >&2 "ERROR: Backend not built! Build the project before running this script."
  exit 1
fi
if [[ ! -d ${dir_frontend}/docker/build ]]
then
  echo >&2 "ERROR: Frontend not built! Build the project before running this script."
  exit 1
fi

command -v oc > /dev/null 2>&1 || {
  echo >&2 "ERROR: The oc client tool needs to be installed to connect to OpenShift."
  exit 1
}

[[ -x $(command -v oc) ]] || {
  echo >&2 "ERROR: The oc client tool is not executable. Please make it executable by running \
‘chmod u+x \$(command -v oc)’."
  exit 1
}

# Print info about the current user and project
echo "Current user: $(oc whoami)"
# Check that the current user has at least one project
[[ -z "$(oc projects -q)" ]] && {
  echo >&2 "You have no projects. Use ‘oc new-project <project-name>’ to create one."
  exit 1
}
# Display info about the current project
oc project

# Check that the current project is empty
get_all=$(oc get all -o name)
if [[ -z "$get_all" ]]
then
  echo "The project appears to be empty."
else
  echo >&2
  echo >&2 "Project content:"
  echo >&2
  echo >&2 "$get_all"
  echo >&2
  echo >&2 "ERROR: The project is not empty."
  exit 1
fi

echo
echo -e "$summary"
echo

declare -l answer_continue # -l converts the value to lower case before it's assigned
read -r -p "Do you want to continue? [y/N]: " "answer_continue"
[[ "$answer_continue" == "y" ]] || {
  echo "Aborted."
  exit 0
}

# Set up PostgreSQL
oc new-app --name postgresql postgresql-persistent

# Backend
# -- binary build (upload local artifacts + Dockerfile)
oc new-build --name backend --strategy=docker --binary
oc start-build backend --from-dir=${dir_backend} --follow
# -- new app
oc new-app backend
# -- use PostgreSQL secret
oc set env dc/backend --from=secret/postgresql
# -- set the rest of the configuration
oc set env dc/backend "${dc_backend_env[@]}"
# Remove the default emptyDir volume
oc set volumes dc/backend --remove --name backend-volume-1
# Replace it with a PVC
oc set volumes dc/backend --add \
    --type pvc \
    --claim-size 1Gi \
    --claim-mode ReadWriteOnce \
    --name data-local \
    --mount-path /deployments/local

# Frontend
# -- binary build
oc new-build --name frontend --strategy=docker --binary
oc start-build frontend --from-dir=${dir_frontend}/docker --follow
# -- new app
oc new-app frontend
# -- expose the service
oc expose svc/frontend
# -- change target port to 8080
oc patch route frontend -p '{"spec":{"port":{"targetPort":"8080-tcp"}}}'

echo
echo "You can access the application at http://$(oc get route frontend -o custom-columns=:spec.host | tr -d '\n') \
once the deployment is done."
if [[ -v download ]]
then
  echo
  echo "The OSM file download and its processing can take some time depending on its size. \
For large files (hundreds of MB) this can be several minutes. \
During this period, the application informs about backend service being unreachable."
fi
