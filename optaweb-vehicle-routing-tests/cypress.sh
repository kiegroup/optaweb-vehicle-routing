#!/usr/bin/env bash
readonly backend_port=8180

readonly backend_project=optaweb-vehicle-routing-backend
readonly frontend_project=optaweb-vehicle-routing-frontend

#1. start backend
cd ../${backend_project}
readonly test_osm_file="planet_12.032,53.0171_12.1024,53.0491.osm.pbf"
readonly local_osm_directory=local/openstreetmap

[[ ! -d ${local_osm_directory} ]] && mkdir -p ${local_osm_directory}
cp src/test/resources/org/optaweb/vehiclerouting/plugin/routing/${test_osm_file} \
    ${local_osm_directory}

readonly backend_properties="-Dapp.region.country-codes=DE -Dapp.routing.osm-file=${test_osm_file} -Dserver.port=${backend_port}"
readonly project_version=$(mvn -q help:evaluate -Dexpression=project.version -DforceStdout)
java ${backend_properties} -jar target/${backend_project}-${project_version}.jar > ${backend_project}.log 2>&1 &
readonly backend_pid=$!

#2. start frontend
readonly frontend_port=3100

cd ../${frontend_project}
REACT_APP_BACKEND_URL=http://localhost:${backend_port} PORT=${frontend_port} node/npm run start > ${frontend_project}.log 2>&1 &
readonly frontend_pid=$!

# wait for the frontend to become available
PORT=${frontend_port} node/npm run wait

#3. run cypress tests
readonly cypress_docker_image=cypress/included:3.6.0

docker run \
  --network=host \
  -v \
  ${PWD}:/e2e:Z \
  -w /e2e \
  --entrypoint cypress ${cypress_docker_image} run --project . --config baseUrl=http://localhost:${frontend_port}

#4. kill all running processes
kill ${backend_pid}
readonly pgid=$(ps opgid= ${frontend_pid})
kill -TERM -- -${pgid}
