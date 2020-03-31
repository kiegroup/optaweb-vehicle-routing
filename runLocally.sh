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

function list() {
  for i in "$1"/*
  do
    echo "* $(basename \""${i}")";
  done
}

function confirm() {
  declare -l answer # -l converts the value to lower case before it's assigned
  read -r -p "$1 [y/N]: " "answer"
  [[ "$answer" == "y" ]]
}

function abort() {
  echo "Aborted."
  exit 0
}

function standalone_jar_or_maven() {
  echo
  echo "Getting project version..."
  readonly version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  echo "Project version: ${version}"

  readonly standalone=optaweb-vehicle-routing-standalone
  readonly jar=${standalone}/target/${standalone}-${version}.jar

  if [[ ! -f ${jar} ]]
  then
    if confirm "Jarfile ‘$jar’ does not exist. Run Maven build now?"
    then
      if ! ./mvnw clean install -DskipTests
      then
        echo >&2 "Maven build failed. Aborting the script."
        exit 1
      fi
    else
      abort
    fi
  fi
}

function download() {
  curl "http://download.geofabrik.de/$1" -o "$2"
  echo
  echo "Created $2."
}

function country_code() {
  local region=${1%.osm.pbf}
  region=${region%-latest}
  local cc_file=${cc_dir}/${region}

  if [[ ! -f ${cc_file} ]]
  then
    region=${region//-/ }

    local cc_tag="nv-i18n-1.27"
    local cc_java="$cc_dir/CountryCode-$cc_tag.java"
    [[ ! -f ${cc_java} ]] && \
      curl https://raw.githubusercontent.com/TakahikoKawasaki/nv-i18n/${cc_tag}/src/main/java/com/neovisionaries/i18n/CountryCode.java -s > "$cc_java"

    cc=$(grep -i "$region.*OFFICIALLY_ASSIGNED" "$cc_java" | sed 's/ *\(..\).*/\1/')

    [[ -d ${cc_dir} ]] || mkdir "$cc_dir"
    echo "$cc" > "$cc_file"
  fi
}

function interactive() {
  echo
  echo "Downloaded OpenStreetMap files:"
  list "${osm_dir}"

  echo
  echo "Road network graphs imported:"
  list "${gh_dir}"

  echo
  confirm "Do you want to download more?" && {
    # TODO other regions than Europe
    readonly europe=local/europe.html
    # TODO refresh daily
    [[ ! -f ${europe} ]] && curl http://download.geofabrik.de/europe.html -s > ${europe}

    # TODO check if xmllint is installed

    readarray -t region_hrefs <<< "$(xmllint ${europe} --html --xpath '//tr[@onmouseover]/td[2]/a/@href' | sed 's/.*href="\(.*\)"/\1/')"
    readarray -t region_names <<< "$(xmllint ${europe} --html --xpath '//tr[@onmouseover]/td[1]/a/text()')"
    # TODO size
    for i in "${!region_names[@]}"
    do
      printf "%s\t%s\n" "$i" "${region_names[$i]}";
    done

    declare answer_region_id
    read -r -p "Select a region: " "answer_region_id"

    # TODO validate region index
    local osm_file=${region_hrefs[answer_region_id]##*/}
    local osm_target=${osm_dir}/${osm_file}

    # TODO skip if already downloaded

    download "${region_hrefs[answer_region_id]}" "$osm_target"
    country_code "$osm_file"
  }

  standalone_jar_or_maven

  confirm "Do you want launch OptaWeb Vehicle Routing?" || abort

}

function quickstart() {
  local subregion="europe"
  local demo_osm_file="belgium-latest.osm.pbf"
  local osm_target=${osm_dir}/${demo_osm_file}
  if [[ ! -f ${osm_target} ]]
  then
    echo "OptaWeb Vehicle Routing needs an OSM file for distance calculation. \
It contains a built-in dataset for $demo_osm_file, which does not exist in $osm_dir. \
This script can download it for you from Geofabrik.de."
    confirm "Download $demo_osm_file from Geofabrik.de now?" || abort
    download "$subregion/$demo_osm_file" "$osm_target"
  fi
}

readonly last_vrp_dir_file=.VRP_DIR_LAST

if [[ -f ${last_vrp_dir_file} ]]
then
  readonly last_vrp_dir=$(cat ${last_vrp_dir_file})
else
  readonly last_vrp_dir=""
fi

if [[ -z ${last_vrp_dir} ]]
then
  readonly vrp_dir=$HOME/.vrp
  echo "There is no last used VRP dir. Using the default."
else
  readonly vrp_dir=${last_vrp_dir}
fi

echo "VRP dir: $vrp_dir"

if [[ ! -d ${vrp_dir} ]]
then
  confirm "VRP dir ‘$vrp_dir’ does not exist. Do you want to create it now?" || abort
  mkdir ${vrp_dir} || {
    echo >&2 "Cannot create VRP directory ‘$vrp_dir’."
    exit 1
  }
fi

# Remember VRP dir
echo ${vrp_dir} > ${last_vrp_dir_file}

readonly osm_dir=${vrp_dir}/openstreetmap
readonly gh_dir=${vrp_dir}/graphhopper
readonly cc_dir=${vrp_dir}/country_codes

case $1 in
  -i | --interactive)
    interactive
  ;;
  *)
    quickstart
  ;;
esac

standalone_jar_or_maven

java -jar "${standalone}/target/${standalone}-${version}.jar" \
 "--app.routing.osm-dir=$osm_dir" \
 "--app.routing.gh-dir=$gh_dir" \
 "--app.persistence.h2-dir=$vrp_dir/db"
