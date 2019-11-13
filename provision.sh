#!/usr/bin/env bash
set -e

readonly script_name=$(basename "$0")

function wrong_args() {
  echo >&2 "Wrong number of arguments! Usage:"
  echo >&2 "  $script_name"
  echo >&2 "  $script_name osm-file-name country-code-list [osm-file-download-url]"
  echo >&2
  echo >&2 "Run $script_name --help for more information."
  exit 1
}

function usage() {
  echo "Usage:"
  echo "  $script_name"
  echo "  $script_name osm-file-name country-code-list [osm-file-download-url]"
  echo
  echo "Example 1."
  echo "  $script_name"
  echo
  echo "  Sets up backend to run in air distances routing mode."
  echo "  WARNING: Air mode doesn't give reliable values. \
It's only useful for evaluation, debugging, or incremental setup purpose."
  echo
  echo
  echo "Example 2."
  echo "  $script_name belgium-latest.osm.pbf BE"
  echo
  echo "  Use GraphHopper routing mode and upload OSM file or GraphHopper data manually. \
The backend will be initially configured to air routing mode. When it starts for the first time, \
use it to upload Belgium OSM file or GraphHopper data to the claimed Persistent Volume. For example:"
  echo
  echo "      oc rsync ~/.vrp/openstreetmap/belgium-latest.osm.pbf backend-1-7k5pl:/deployments/local/openstreetmap/"
  echo
  echo "  if you haven't run the application locally and you just have the OSM file that hasn't been processed by \
GraphHopper into a routing graph."
  echo "  If you have run the application, you can upload the processed graph to skip the processing step in OpenShift:"
  echo
  echo "      oc rsync ~/.vrp/graphhopper/belgium-latest backend-1-7k5pl:/deployments/local/graphhopper/"
  echo
  echo "  When the upload is complete. Change routing engine to GraphHopper:"
  echo
  echo "      oc set env dc/backend APP_ROUTING_ENGINE=graphhopper"
  echo
  echo "  The deployment config change will trigger a new pod with the updated environment, \
which will replace the old pod."
  echo
  echo "Example 3."
  echo "  $script_name belgium-latest.osm.pbf BE http://download.geofabrik.de/europe/belgium-latest.osm.pbf"
  echo
  echo "  Sets up backend to run in GraphHopper routing mode and downloads an OSM file from the provided URL \
on startup. This provides the best out-of-the-box experience. No additional manual steps."
  exit 0
}

[[ $1 == "--help" ]] && usage

# Check number of arguments and choose summary or display usage
case $# in
  0)
    summary="No routing config provided. The backend will start in air routing mode.\n\n\
WARNING: Air mode doesn't give reliable values. \
It's only useful for evaluation, debugging, or incremental setup purpose. \
You can run ‘$script_name --help’ to see other options."
    ;;
  2)
    summary="The backend pod will start in air mode. Use the pod to upload a graph directory or an OSM file. \
Then change routing mode to graphopper. Run ‘$script_name --help’ for more info."
    ;;
  3)
    summary="The backend will download an OSM file on startup."
    ;;
  *)
    wrong_args
esac

# Process arguments
declare -a dc_backend_env
if [[ $# -ge 2 ]]
then
  dc_backend_env+=("APP_ROUTING_OSM_FILE=$1")
  dc_backend_env+=("APP_REGION_COUNTRY_CODES=$2")
fi
if [[ $# == 3 ]]
then
  dc_backend_env+=("APP_ROUTING_ENGINE=graphhopper")
  dc_backend_env+=("APP_ROUTING_OSM_DOWNLOAD_URL=$3")
else
  dc_backend_env+=("APP_ROUTING_ENGINE=air")
fi

# Change dir to the project root (where provision.sh is located) to correctly resolve module paths.
# This is needed in case the script was called from a different location than the project root.
cd "$(dirname "$(readlink -f "$0")")"

readonly dir_backend=optaweb-vehicle-routing-backend
readonly dir_frontend=optaweb-vehicle-routing-frontend

# Fail fast if the project hasn't been built
if ! stat -t ${dir_backend}/target/*.jar > /dev/null 2>&1
then
  echo >&2 "Backend not built! Build the project before running this script."
  exit 1
fi
if [[ ! -d ${dir_frontend}/docker/build ]]
then
  echo >&2 "Frontend not built! Build the project before running this script."
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
oc new-build --name frontend --strategy=docker --binary -e BACKEND_URL=http://backend:8080
oc start-build frontend --from-dir=${dir_frontend}/docker --follow
# -- new app
oc new-app frontend
# -- expose the service
oc expose svc/frontend
# -- change target port to 8080
oc patch route frontend -p '{"spec":{"port":{"targetPort":"8080-tcp"}}}'

echo "You can access the application at http://$(oc get route frontend -o custom-columns=:spec.host | tr -d '\n') \
once the deployment is done."
