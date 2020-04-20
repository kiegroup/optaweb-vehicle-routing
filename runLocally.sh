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
# If dir is empty, dir/* will expand to "" instead of "dir/*". This is useful when reading regions in interactive() and
# either openstreetmap or graphhopper dir is empty.
shopt -s nullglob

function confirm() {
  declare -l answer # -l converts the value to lower-case before it's assigned
  read -r -p "$1 [y/N]: " answer
  [[ "$answer" == "y" ]]
}

function abort() {
  echo "Aborted."
  exit 0
}

function standalone_jar_or_maven() {
  local -r standalone=optaweb-vehicle-routing-standalone

  # BEGIN: Distribution use case
  #
  # We're running a copy of the script in the project root that has been moved to distribution's bin directory during
  # distribution assembly. The only difference is that the standalone JAR is in the same directory as the script (bin)
  # and project.version is set using resource filtering during assembly.

  # shellcheck disable=SC2154
  if [[ ! -f pom.xml && -f ${standalone}-${project.version}.jar ]]
  then
    readonly jar=${standalone}-${project.version}.jar
    return 0
  fi
  # END: Distribution use case

  echo
  echo "Getting project version..."

  local version
  if command -v mvn > /dev/null 2>&1
  then
    # TODO fix mvnw's stdout handling and replace mvn with mvnw
    local -r version=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
  else
    echo >&2 "WARNING: Maven is not installed (mvn is not on \$PATH). \
The script will grep pom.xml for project version, which is not as reliable as using Maven."
    local -r version=$(grep '<parent> *$' pom.xml -A4 | grep version | sed 's;.*<version>\(.*\)</version>.*;\1;')
  fi

  [[ -n ${version} ]] || {
    echo "ERROR: Invalid project version: ‘$version’."
    exit 1
  }

  echo "Project version: ${version}"

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
  local -r osm_file_path=${osm_dir}/${osm_file}
  local -r gh_graph_path=${gh_dir}/${osm_file%.osm.pbf}
  [[ -f "$osm_file_path" || -d "$gh_graph_path" ]]
}

function run_optaweb() {
  declare -a args
  args+=("--app.persistence.h2-dir=$vrp_dir/db")
  args+=("--app.routing.engine=$routing_engine")
  if [[ ${routing_engine} == "graphhopper" ]]
  then
    args+=("--app.routing.osm-dir=$osm_dir")
    args+=("--app.routing.gh-dir=$gh_dir")
    args+=("--app.routing.osm-file=$osm_file")
  fi
  # Avoid empty country-codes - that would be an invalid argument.
  if [[ -z ${cc_list} ]]
  then
    # This is the correct way to set a property to empty value. "--property=" is an invalid syntax in Spring Boot.
    args+=("--app.region.country-codes")
  else
    [[ ${cc_list} != "??" ]] && args+=("--app.region.country-codes=$cc_list")
  fi
  java -jar "$jar" "${args[@]}"
}

function download() {
  echo "Downloading $1..."
  curl -L "$1" -o "$2"
  echo
  echo "Created $2."
}

function country_code() {
  local -r region=${1%.osm.pbf}
  local -r cc_file=${cc_dir}/${region}
  local -r cc_tag="nv-i18n-1.27"
  local -r cc_java="$cache_dir/CountryCode-$cc_tag.java"

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
    local region_search=${region%-latest}
    region_search=${region_search//-/ }

    cc=$(grep -i "$region_search.*OFFICIALLY_ASSIGNED" "$cc_java" | sed 's/ *\(..\).*/\1/')

    echo "$cc" > "$cc_file"
  fi
}

function download_menu() {
  local -r url=$1
  local -r url_parent=${url%/*} # remove shortest suffix matching "/*" => http://download.geofabrik.de/north-america/us
  local -r url_html=${url##*/} # index.html, europe.html, etc.
  local -r super_region_file="$cache_geofabrik/$url_html"
  local -r sub_region_osm_url=$2

  # TODO refresh daily
  if [[ ! -f ${super_region_file} || ! -s ${super_region_file} ]]
  then
    curl --silent --show-error 2>>"$cache_geofabrik/error.log" "$url" > "$super_region_file" || {
      echo "ERROR: Cannot download from Geofabrik. Are you offline?"
      exit 1
    }
  fi

  command -v xmllint > /dev/null 2>&1 || {
    echo >&2 "ERROR: xmllint is not installed. Please install xmllint and retry."
    exit 1
  }

  readarray -t region_names <<< "$(xmllint 2>>"$error_log" "$super_region_file" --html --xpath '//tr[@onmouseover]/td[1]/a/text()')"
  readarray -t region_sub_hrefs <<< "$(xmllint 2>>"$error_log" "$super_region_file" --html --xpath '//tr[@onmouseover]/td[1]/a/@href' | sed 's/.*href="\(.*\)"/\1/')"
  readarray -t region_osm_hrefs <<< "$(xmllint 2>>"$error_log" "$super_region_file" --html --xpath '//tr[@onmouseover]/td[2]/a/@href' | sed 's/.*href="\(.*\)"/\1/')"
  readarray -t region_sizes <<< "$(xmllint 2>>"$error_log" "$super_region_file" --html --xpath '//tr[@onmouseover]/td[3]/text()' | sed 's/.*(\(.*\))/\1/')"


  # Make the array empty if it contains just 1 empty element.
  [[ ${#region_names[*]} == 1 && -z ${region_names[0]} ]] && region_names=()

  local -r max=$((${#region_names[*]} - 1))

  if [[ ${max} -lt 0 ]]
  then
    echo
    echo "This region has no sub-regions to choose from."
    echo
    confirm "Do you want to download $sub_region_osm_url?" && download "$sub_region_osm_url" "$osm_dir/${sub_region_osm_url##*/}"
    return 0
  fi

  declare answer_region_id
  declare answer_action

  local -r format=" %2s %-30s %10s\n"
  local -r width=46

  while true
  do
    echo
    # shellcheck disable=SC2059
    printf "$format" "#" "REGION" "SIZE"
    printf "%.s=" $(seq 1 "$width")
    printf "\n"

    for i in "${!region_names[@]}"
    do
      # shellcheck disable=SC2059
      printf "$format" "$i" "${region_names[$i]}" "${region_sizes[$i]}";
    done

    read -r -p "Select a region (0-$max) or Enter to go back: " answer_region_id

    [[ -z ${answer_region_id} ]] && break

    if [[ ${answer_region_id} != [0-9] && ${answer_region_id} != [1-9][0-9] || ${answer_region_id} -gt ${max} ]]
    then
      echo "Wrong region ID ‘$answer_region_id’."
      continue
    fi

    read -r -p "Download (d) or enter (e): " answer_action
    if [[ ${answer_action} != [de] ]]
    then
      echo "Wrong action ‘$answer_action’."
      continue
    fi

    break
  done

  [[ -z ${answer_region_id} ]] && return 0

  # osm_url is used either to download an OSM in the d) case or to pass it to next download_menu level
  # to make it possible to download it if there are no sub-regions to choose from in the next step.
  local -r osm_url=${url_parent}/${region_osm_hrefs[answer_region_id]}
  local -r sub_region_html_url=${url_parent}/${region_sub_hrefs[answer_region_id]}

  case ${answer_action} in
    e)
      download_menu "$sub_region_html_url" "$osm_url"
    ;;
    d)
      # Remove region prefix (e.g. europe/) from href to get the OSM file name.
      local -r osm_file=${osm_url##*/}
      local -r osm_target=${osm_dir}/${osm_file}

      if [[ -f ${osm_target} ]]
      then
        echo "Already downloaded."
      else
        download "$osm_url" "$osm_target"
        # Hack to set country code of any US state.
        if [[ ${osm_url}/ == */north-america/us/* ]]
        then
          echo "US" > "$cc_dir/${osm_file%.osm.pbf}"
        fi
      fi
    ;;
    *)
      echo "ERROR: Not possible (region_id=$answer_region_id,action=$answer_action)."
      exit 1
    ;;
  esac
}

function interactive() {
  while true
  do
    readarray -t regions <<< "$(for r in "$osm_dir"/* "$gh_dir"/*; do basename "$r" | sed 's/.osm.pbf//'; done | sort | uniq)"

    # Make the array empty if it contains just 1 empty element.
    [[ ${#regions[*]} == 1 && -z ${regions[0]} ]] && regions=()

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
      local region=${regions[$i]}
      # pass cc_error to skip repeated curl in this loop
      country_code "$region" "$cc_status" || cc_status="ERROR"
      # shellcheck disable=SC2059
      printf "$format" \
        "$i" \
        "$region" \
        "$(if [[ -f "$osm_dir/$region.osm.pbf" ]]; then echo "[x]"; else echo "[ ]"; fi)" \
        "$(if [[ -d "$gh_dir/$region" ]]; then echo "[x]"; else echo "[ ]"; fi)" \
        "$(cat "$cc_dir/$region")"
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
    echo "q:    Quit."
    [[ ${max} -ge 0 ]] && echo "0-$max: Select a region and run OptaWeb Vehicle Routing."

    echo
    local -l command # -l converts to lower-case
    read -r -p "Your choice: " command
    case "$command" in
      q)
        exit 0
      ;;
      d)
        download_menu "http://download.geofabrik.de/index.html"
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
  local url=http://download.geofabrik.de/europe/belgium-latest.osm.pbf
  osm_file="belgium-latest.osm.pbf"
  cc_list="BE"
  local -r osm_target=${osm_dir}/${osm_file}
  if [[ ! -f ${osm_target} ]]
  then
    echo "OptaWeb Vehicle Routing needs an OSM file for distance calculation. \
It contains a built-in dataset for $osm_file, which does not exist in $osm_dir. \
This script can download it for you from Geofabrik.de."
    confirm "Download $osm_file from Geofabrik.de now?" || abort
    download "$url" "$osm_target"
    echo "$cc_list" > "$cc_dir/${osm_file%.osm.pbf}"
  fi
  standalone_jar_or_maven
  run_optaweb
}

# Change dir to the project root (where the script is located).
# This is needed to correctly resolve .VRP_DIR_LAST, path to the standalone JAR, etc.
# in case the script was called from a different location than the project root.
cd "$(dirname "$(readlink -f "$0")")"

readonly last_vrp_dir_file=.DATA_DIR_LAST

if [[ -f ${last_vrp_dir_file} ]]
then
  readonly last_vrp_dir=$(cat ${last_vrp_dir_file})
else
  readonly last_vrp_dir=""
fi

if [[ -z ${last_vrp_dir} ]]
then
  readonly vrp_dir=$HOME/.optaweb-vehicle-routing
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
readonly cache_dir=${vrp_dir}/.cache
readonly cache_geofabrik=${cache_dir}/geofabrik

[[ -d ${osm_dir} ]] || mkdir "$osm_dir"
[[ -d ${gh_dir} ]] || mkdir "$gh_dir"
[[ -d ${cc_dir} ]] || mkdir "$cc_dir"
[[ -d ${cache_geofabrik} ]] || mkdir -p ${cache_geofabrik}

declare routing_engine="graphhopper"

# Getting started (semi-interactive) - use OSM compatible with the built-in data set, download if not present.
if [[ $# == 0 ]]
then
  quickstart
  exit 0
fi

# Use air mode (no OSM file, no country codes).
if [[ $1 == "--air" ]]
then
  routing_engine="air"
  standalone_jar_or_maven
  run_optaweb
  exit 0
fi

case $1 in
  -i | --interactive)
    interactive
  ;;
  # Demo use case (non-interactive) - start with existing data.
  [a-z]*)
    region=${1%.osm.pbf}
    osm_file=${region}.osm.pbf
    if ! validate
    then
      region=${region}-latest
      osm_file=${region}.osm.pbf
      validate || {
        echo >&2 "Wrong region ‘$1’. One of the following must exist:"
        echo >&2 "- OSM file: $osm_dir/${1%.osm.pbf}.osm.pbf"
        echo >&2 "- GraphHopper graph: $gh_dir/${1%.osm.pbf}"
        exit 1
      }
    fi

    cc_list=$(cat "$cc_dir/$region")
    standalone_jar_or_maven
    run_optaweb
  ;;
  *)
    echo >&2 "Wrong argument."
    # TODO display help
  ;;
esac
