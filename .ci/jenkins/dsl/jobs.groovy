/*
* This file is describing all the Jenkins jobs in the DSL format (see https://plugins.jenkins.io/job-dsl/)
* needed by the Kogito pipelines.
*
* The main part of Jenkins job generation is defined into the https://github.com/kiegroup/kogito-pipelines repository.
*
* This file is making use of shared libraries defined in
* https://github.com/kiegroup/kogito-pipelines/tree/main/dsl/seed/src/main/groovy/org/kie/jenkins/jobdsl.
*/

import org.kie.jenkins.jobdsl.model.Environment
import org.kie.jenkins.jobdsl.model.Folder
import org.kie.jenkins.jobdsl.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoJobUtils

jenkins_path = '.ci/jenkins'

Map getMultijobPRConfig() {
    return [
        parallel: true,
        buildchain: true,
        jobs : [
            [
                id: 'optaweb-vehicle-routing',
                primary: true,
            ]
        ],
    ]
}

List environments = Environment.getActiveEnvironments(this)
environments.retainAll { it != Environment.NATIVE } // There is no requirement for Native support.
environments.retainAll { it != Environment.MANDREL } // There is no requirement for Native Mandrel support.
KogitoJobUtils.createPerEnvPerRepoPRJobs(this, environments) { jobFolder -> getMultijobPRConfig() }

// Init branch
setupInitBranchJob()


void setupInitBranchJob() {
    def jobParams = KogitoJobUtils.getBasicJobParams(this, 'optaweb-vehicle-routing', Folder.INIT_BRANCH, "${jenkins_path}/Jenkinsfile.init-branch", 'Optaweb Vehicle Routing Init Branch')
    KogitoJobUtils.setupJobParamsDefaultMavenConfiguration(this, jobParams)
    jobParams.env.putAll([
        REPO_NAME: 'optaweb-vehicle-routing',
        JENKINS_EMAIL_CREDS_ID: "${JENKINS_EMAIL_CREDS_ID}",

        GIT_AUTHOR: "${GIT_AUTHOR_NAME}",
        AUTHOR_CREDS_ID: "${GIT_AUTHOR_CREDENTIALS_ID}",

        MAVEN_SETTINGS_CONFIG_FILE_ID: "${MAVEN_SETTINGS_FILE_ID}",
        MAVEN_DEPENDENCIES_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
        MAVEN_DEPLOY_REPOSITORY: "${MAVEN_ARTIFACTS_REPOSITORY}",
    ])
    KogitoJobTemplate.createPipelineJob(this, jobParams)?.with {
        parameters {
            stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

            stringParam('BUILD_BRANCH_NAME', "${GIT_BRANCH}", 'Set the Git branch to checkout')

            stringParam('OPTAPLANNER_VERSION', '', 'OptaPlanner version to set.')

            booleanParam('SEND_NOTIFICATION', false, 'In case you want the pipeline to send a notification on CI channel for this run.')
        }
    }
}