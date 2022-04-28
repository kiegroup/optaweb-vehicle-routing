#!/usr/bin/env bash

readonly CONTAINER_RUNTIME=$1
readonly FRONTEND_PROJECT_PATH=$2
readonly VERSION_CYPRESS=$3
readonly APPLICATION_PORT=$4

user_command="--user  $(id -u):$(id -g)"

if [[ "$CONTAINER_RUNTIME" == "podman" ]]
then
    user_command="${user_command} --userns=keep-id"
fi

$CONTAINER_RUNTIME run --network=host \
                          -v $FRONTEND_PROJECT_PATH:/e2e:Z \
                          $user_command \
                          -w /e2e --entrypoint cypress docker.io/cypress/included:$VERSION_CYPRESS \
                          run --project . --config baseUrl=http://localhost:$APPLICATION_PORT