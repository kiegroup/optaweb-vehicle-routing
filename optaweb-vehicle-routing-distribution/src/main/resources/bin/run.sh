#!/usr/bin/env bash

script_dir="$(dirname "$(readlink -f "$0")")"
if [[ $# == 0 ]]
then
  echo "In order to run in road distance mode, you need to provide a ‘VRP directory’ with the following structure:"
  echo ""
  echo "    VRP_DIR"
  echo "    ├── graphhopper   <-- road network graph constructed from an OSM file will be stored here"
  echo "    └── openstreetmap <-- download an OSM file and save it here, it will be expected here"
  echo ""
  echo "You may skip this and start in air distance mode and without persistence immediately."
  declare -l answer_continue # -l converts the value to lower case before it's assigned
  read -r -p "Continue in air distance mode and without persistence? [y/N]: " "answer_continue"
  [[ "$answer_continue" == "y" ]] || {
    echo "Aborted."
    exit 0
  }
  options="\
--spring.datasource.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false \
--app.persistence.h2-dir=vrp \
--app.routing.engine=air"

elif [[ $# == 3 ]]
then
  engine="graphhopper"
  vrp_dir=$1
  osm_file=$2
  country_codes=$3
  options="\
--app.persistence.h2-dir=$vrp_dir/db \
--app.routing.engine=graphhopper \
--app.routing.osm-dir=${vrp_dir}/openstreetmap \
--app.routing.osm-file=${osm_file} \
--app.routing.gh-dir=${vrp_dir}/graphhopper \
--app.region.country-codes=${country_codes}"

else
  echo >&2 "Wrong number of arguments. Usage:"
  echo >&2 "  ./$(basename "$0") VRP_DIR OSM_FILE COUNTRY_CODES"
  echo >&2
  echo >&2 "Example:"
  echo >&2 "  ./$(basename "$0") ~/.vrp belgium-latest.osm.pbf BE"
  echo >&2
  echo >&2 "Please read the documentation for more detailed instructions."
  exit 1
fi

java -jar ${script_dir}/optaweb-vehicle-routing-standalone-${project.version}.jar \
--server.address=localhost \
--server.port=8080 \
${options}
