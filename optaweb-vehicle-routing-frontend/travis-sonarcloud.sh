#!/usr/bin/env bash

# This script handles different invocation of SonarQube Scanner
# for pull requests and for push/cron builds (e.g. on master branch).
#
# This complexity could be avoided by using Travis' SonarCloud addon [1]
# but that doesn't support pull requests from forks at this moment.
#
# [1] https://docs.travis-ci.com/user/sonarcloud/

if [[ "x$SONARCLOUD_TOKEN" != x ]]
then
  args[0]="-Dsonar.login=$SONARCLOUD_TOKEN"
  if [[ "$TRAVIS_EVENT_TYPE" == "pull_request" ]]
  then
    args[1]="-Dsonar.pullrequest.base=$TRAVIS_BRANCH"
    args[2]="-Dsonar.pullrequest.branch=$TRAVIS_PULL_REQUEST_BRANCH"
    args[3]="-Dsonar.pullrequest.key=$TRAVIS_PULL_REQUEST"
  fi
  npx sonarqube-scanner "${args[@]}"
fi
