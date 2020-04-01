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

# Abort script if any simple command outside an if, while, &&, ||, etc. exists with a nonzero code.
set -e

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
  if command -v mvn > /dev/null 2>&1
  then
    # TODO fix mvnw's stdout handling and replace mvn with mvnw
    readonly version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  else
    echo >&2 "WARNING: Maven is not installed (mvn is not on \$PATH). \
The script will grep pom.xml for project version, which is not as reliable as using Maven."
    readonly version=$(grep '<parent> *$' pom.xml -A4 | grep version | sed 's;.*<version>\(.*\)</version>.*;\1;')
  fi

  [[ -n ${version} ]] || {
    echo "ERROR: Invalid project version: ‘$version’."
    exit 1
  }

  echo "Project version: ${version}"

  readonly standalone=optaweb-vehicle-routing-standalone
  readonly jar=${standalone}/target/${standalone}-${version}.jar

  if [[ ! -f ${jar} ]]
  then
    confirm "Jarfile ‘$jar’ does not exist. Run Maven build now?" || abort
    if ! ./mvnw clean install -DskipTests
    then
      echo >&2 "Maven build failed. Aborting the script."
      exit 1
    fi
  fi
}

function validate() {
  local osm_file_path=${osm_dir}/${osm_file}
  local gh_graph_path=${gh_dir}/${osm_file%.osm.pbf}
  if [[ ! -f "$osm_file_path" && ! -d "$gh_graph_path" ]]
  then
    echo >&2 "Wrong region ‘$osm_file’. One of the following must exist:"
    echo >&2 "- OSM file: $osm_file_path"
    echo >&2 "- GraphHopper graph: $gh_graph_path"
    exit 1
  fi
}

function run_optaweb() {
  declare -a args
  args+=("--app.persistence.h2-dir=$vrp_dir/db")
  args+=("--app.routing.osm-dir=$osm_dir")
  args+=("--app.routing.gh-dir=$gh_dir")
  args+=("--app.routing.osm-file=$osm_file")
  # Avoid empty country-codes - that would be an invalid argument.
  [[ -n ${cc_list} && ${cc_list} != "??" ]] && args+=("--app.region.country-codes=$cc_list")
  java -jar "${standalone}/target/${standalone}-${version}.jar" "${args[@]}"
}

function download() {
  local osm_url="http://download.geofabrik.de/$1"
  echo "Downloading $osm_url..."
  curl "$osm_url" -o "$2"
  echo
  echo "Created $2."
}

function country_code() {
  local region=${1%.osm.pbf}
  local cc_file=${cc_dir}/${region}
  local cc_tag="nv-i18n-1.27"
  local cc_java="$vrp_dir/CountryCode-$cc_tag.java"

  [[ -d ${cc_dir} ]] || mkdir "$cc_dir"

  # If an error has occurred in the list_downloads loop, mark this region's code as "unknown".
  [[ $2 == "ERROR" ]] && echo "??" > "$cc_file"

  if [[ (! -f ${cc_java} || -f ${cc_java}.err) && $2 != "ERROR" ]]
  then
    if curl 2>>"$cc_java.err" > "$cc_java" --silent --show-error \
https://raw.githubusercontent.com/TakahikoKawasaki/nv-i18n/${cc_tag}/src/main/java/com/neovisionaries/i18n/CountryCode.java
    then
      rm "$cc_java.err"
    else
      # mark this region's code as "unknown"
      [[ ! -f ${cc_file} ]] && echo "??" > "$cc_file"
      # and report error
      return 1
    fi
  fi

  # If this loop instance doesn't have an error and cc_file doesn't exist yet or its content is "unknown".
  if [[ $2 != ERROR && ((! -f ${cc_file}) || $(cat "$cc_file") == "??") ]]
  then
    region=${region%-latest}
    region=${region//-/ }

    cc=$(grep -i "$region.*OFFICIALLY_ASSIGNED" "$cc_java" | sed 's/ *\(..\).*/\1/')

    echo "$cc" > "$cc_file"
  fi
}

function list_downloads() {
  # TODO other regions than Europe
  local -r europe=local/europe.html
  # TODO refresh daily
  [[ ! -f ${europe} ]] && curl http://download.geofabrik.de/europe.html -s > ${europe}

  # TODO check if xmllint is installed

  readarray -t region_hrefs <<< "$(xmllint 2>>"$error_log" ${europe} --html --xpath '//tr[@onmouseover]/td[2]/a/@href' | sed 's/.*href="\(.*\)"/\1/')"
  readarray -t region_names <<< "$(xmllint 2>>"$error_log" ${europe} --html --xpath '//tr[@onmouseover]/td[1]/a/text()')"
  # TODO size

  local max=$((${#region_names[*]} - 1))
  declare answer_region_id

  while true
  do
    for i in "${!region_names[@]}"
    do
      printf "%s\t%s\n" "$i" "${region_names[$i]}";
    done

    read -r -p "Select a region (0-$max) or Enter to go back: " "answer_region_id"

    [[ -z ${answer_region_id} ]] && break

    if [[ ${answer_region_id} != [0-9] && ${answer_region_id} != [1-9][0-9] || ${answer_region_id} -gt ${max} ]]
    then
      echo "Wrong region ID ‘$answer_region_id’."
      continue
    fi

    break
  done

  [[ -z ${answer_region_id} ]] && return 0

  # Remove region prefix (e.g. europe/) from href to get the OSM file name.
  local osm_file=${region_hrefs[answer_region_id]##*/}
  local osm_target=${osm_dir}/${osm_file}

  # TODO skip if already downloaded

  download "${region_hrefs[answer_region_id]}" "$osm_target"
}

function interactive() {
  while true
  do
    readarray -t regions <<< "$(for r in "$osm_dir"/* "$gh_dir"/*; do basename "$r" | sed 's/.osm.pbf//'; done | sort | uniq)"

    local format=" %2s %-24s %10s %10s %10s\n"
    local width=62

    echo
    # shellcheck disable=SC2059
    printf "$format" "#" "REGION" "OSM" "GRAPH" "COUNTRY"
    printf "%.s=" $(seq 1 "$width")
    printf "\n"

    local cc_status="OK"

    for i in "${!regions[@]}"
    do
      local r=${regions[$i]}
      # pass cc_error to skip repeated curl in this loop
      country_code "$r" "$cc_status" || cc_status="ERROR"
      # shellcheck disable=SC2059
      printf "$format" \
        "$i" \
        "$r" \
        "$(if [[ -f "$osm_dir/$r.osm.pbf" ]]; then echo "[x]"; else echo "[ ]"; fi)" \
        "$(if [[ -d "$gh_dir/$r" ]]; then echo "[x]"; else echo "[ ]"; fi)" \
        "$(cat "$cc_dir/$r")"
    done

    if [[ ${cc_status} == "ERROR" ]]
    then
      echo
      echo "ERROR: Failed to download country codes. Are you offline?"
    fi

    local max=$((${#regions[*]} - 1))

    echo
    echo "Choose the next step:"
    echo "d:    Download new region."
    echo "0-$max: Select a region and run OptaWeb Vehicle Routing."

    echo
    declare -l command
    read -r -p "Your choice: " "command"
    case "$command" in
      d)
        list_downloads
        continue
      ;;
      [0-9] | [1-9][0-9])
        if [[ ${command} -gt ${max} ]]
        then
          echo "Wrong number: $command"
          continue
        fi
        osm_file=${regions[$command]}.osm.pbf
        cc_list=$(cat "$cc_dir/${regions[$command]}")
        break
      ;;
      *)
        echo "Wrong command."
        continue
      ;;
    esac
  done

  echo "Region: $osm_file"
  echo "Country code list: $cc_list"
  echo
  confirm "Do you want launch OptaWeb Vehicle Routing?" || abort

  standalone_jar_or_maven
  run_optaweb
}

function quickstart() {
  osm_file="belgium-latest.osm.pbf"
  cc_list="BE"
  local subregion="europe"
  local osm_target=${osm_dir}/${osm_file}
  if [[ ! -f ${osm_target} ]]
  then
    echo "OptaWeb Vehicle Routing needs an OSM file for distance calculation. \
It contains a built-in dataset for $osm_file, which does not exist in $osm_dir. \
This script can download it for you from Geofabrik.de."
    confirm "Download $osm_file from Geofabrik.de now?" || abort
    download "$subregion/$osm_file" "$osm_target"
  fi
  standalone_jar_or_maven
  run_optaweb
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

readonly error_log=${vrp_dir}/error.log
rm -f ${error_log}

readonly osm_dir=${vrp_dir}/openstreetmap
readonly gh_dir=${vrp_dir}/graphhopper
readonly cc_dir=${vrp_dir}/country_codes

# Getting started (semi-interactive) - use OSM compatible with the built-in data set, download if not present.
if [[ $# == 0 ]]
then
  quickstart
  exit 0
fi

case $1 in
  -i | --interactive)
    interactive
  ;;
  # Demo use case (non-interactive) - start with existing data.
  [a-z]*)
    region=${1%.osm.pbf}
    region=${region%-latest}-latest
    osm_file=${region}.osm.pbf
    validate
    cc_list=$(cat "$cc_dir/$region")
    standalone_jar_or_maven
    run_optaweb
  ;;
  *)
    echo >&2 "Wrong argument."
    # TODO display help
  ;;
esac
