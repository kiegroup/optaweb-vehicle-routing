#!/usr/bin/env bash

# This script handles different invocation of SonarQube Scanner
# for pull requests and for push/cron builds (e.g. on master branch).
#
# This complexity could be avoided by using Travis' SonarCloud addon [1]
# but that doesn't support pull requests from forks at this moment.
#
# Static properties are defined in sonar-project.properties file.
# https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner
#
# [1] https://docs.travis-ci.com/user/sonarcloud/

if [[ "x$SONARCLOUD_TOKEN" != x ]]
then
  args[0]="-Dsonar.login=$SONARCLOUD_TOKEN"
  if [[ "$TRAVIS_EVENT_TYPE" == "pull_request" ]]
  then
    # https://docs.sonarqube.org/latest/analysis/pull-request/
    args[1]="-Dsonar.pullrequest.base=$TRAVIS_BRANCH"
    args[2]="-Dsonar.pullrequest.branch=$TRAVIS_PULL_REQUEST_BRANCH"
    args[3]="-Dsonar.pullrequest.key=$TRAVIS_PULL_REQUEST"
    # Disabled until https://community.sonarsource.com/t/sonarcloud-pull-request-integration-with-multiple-builds/1992
    # args[4]="-Dsonar.pullrequest.provider=github"
    # args[5]="-Dsonar.pullrequest.github.repository=$TRAVIS_PULL_REQUEST_SLUG"
  else
    # https://docs.sonarqube.org/latest/branches/overview/
    args[1]="-Dsonar.branch.name=$TRAVIS_BRANCH"
  fi
  npx sonarqube-scanner "${args[@]}"
fi
