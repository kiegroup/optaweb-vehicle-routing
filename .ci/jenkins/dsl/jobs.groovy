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
import org.kie.jenkins.jobdsl.KogitoJobUtils

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
