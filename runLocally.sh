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

readonly latest_vrp_dir_file=.VRP_DIR_LAST

if [[ ! -f ${latest_vrp_dir_file} ]]
then
  echo >&2 "Last VRP dir unknown."
  exit 1
fi

readonly vrp_dir=$(cat ${latest_vrp_dir_file})
echo "VRP dir: $vrp_dir"

if [[ ! -d ${vrp_dir} ]]
then
  echo >&2 "VRP dir ‘$vrp_dir’ does not exist."
  exit 1
fi

readonly osm_dir=${vrp_dir}/openstreetmap
readonly gh_dir=${vrp_dir}/graphhopper

function list() {
  for i in "$1"/*; do echo "* $(basename \""${i}")"; done
}

echo
echo "Downloaded OpenStreetMap files:"
list "${osm_dir}"

echo
echo "Road network graphs imported:"
list "${gh_dir}"

echo
declare -l answer_download # -l converts the value to lower case before it's assigned
read -r -p "Do you want to download more? [y/N]: " "answer_download"
[[ "$answer_download" == "y" ]] && {
  # TODO other regions than Europe
  readonly europe=local/europe.html
  # TODO refresh daily
  [[ ! -f ${europe} ]] && curl http://download.geofabrik.de/europe.html -s > ${europe}

  # TODO check if xmllint is installed

  readarray -t region_hrefs <<< "$(xmllint ${europe} --html --xpath '//tr[@onmouseover]/td[2]/a/@href' | sed 's/.*href="\(.*\)"/\1/')"
  readarray -t region_names <<< "$(xmllint ${europe} --html --xpath '//tr[@onmouseover]/td[1]/a/text()')"
  # TODO size
  for i in "${!region_names[@]}"; do printf "%s\t%s\n" "$i" "${region_names[$i]}"; done

  declare answer_region_id
  read -r -p "Select a region: " "answer_region_id"

  # TODO validate region index

  readonly osm_target="${osm_dir}/${region_hrefs[answer_region_id]##*/}"

  # TODO skip if already downloaded

  curl "http://download.geofabrik.de/${region_hrefs[answer_region_id]}" -o "${osm_target}"
  echo
  echo "Created $osm_target."
}

echo
echo "Getting project version..."
readonly version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Project version: ${version}"

readonly standalone=optaweb-vehicle-routing-standalone
readonly jar=${standalone}/target/${standalone}-${version}.jar

if [[ ! -f ${jar} ]]
then
  echo >&2 "Jarfile ‘$jar’ does not exist."
  exit 1
fi

declare -l answer_continue # -l converts the value to lower case before it's assigned
read -r -p "Do you want to continue? [y/N]: " "answer_continue"
[[ "$answer_continue" == "y" ]] || {
  echo "Aborted."
  exit 0
}

java -jar "${standalone}/target/${standalone}-${version}.jar" \
"--app.routing.osm-dir=$osm_dir" \
"--app.routing.gh-dir=$gh_dir" \
"--app.persistence.h2-dir=$vrp_dir/db"
